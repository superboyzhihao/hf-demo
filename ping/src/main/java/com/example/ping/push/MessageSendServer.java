package com.example.ping.push;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import javax.annotation.PostConstruct;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.time.Duration;
import java.util.Objects;

/**
 * @author zzh
 * @date 2024/09/10
 */
@Component
public class MessageSendServer {
    private static final Logger logger = LoggerFactory.getLogger(MessageSendServer.class);
    @Autowired
    private WebClient webClient;
    private static final String LOCK_FILE_NAME = "ping.lock";
    private static final int MAX_CONCURRENT_REQUESTS = 2;

    public void pushMessages() {

        Flux.interval(Duration.ofSeconds(1)) // 一秒发送一条信息
                .map(tick -> "hello")
                .flatMap(t->{
                    //获取锁
                    FileLock fileLock=getLockTry();
                   //如果锁后休眠，让其他进程获取文件锁发送成功，一秒发送请求两个进程随机请求成功
                    if (fileLock!=null) {
                        return pongPushInit(t,fileLock);
                    }else {
                        logger.error("Speed limited");
                        return Mono.just("Request not sent");
                    }
                }) // 推送数据到消费者服务
                .subscribe(response ->{
                                logger.info("接收到pong服务的响应消息为{}",response);
                        } ,
                        error -> logger.error("请求错误"));
    }



    public Mono<String> pongPushInit(String data,FileLock fileLock) {
            return webClient.post()
                    .uri("/receive")
                    .contentType(MediaType.TEXT_PLAIN)
                    .bodyValue(data)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorResume(error -> {
                        return Mono.just("Error occurred while calling pong service");
                    })
                    .doFinally(unLock->{
                        try {
                            logger.info("获取文件锁为======{}",fileLock);
                            fileLock.release();
                            logger.info("释放锁成功");
                        }catch (Exception e){
                            logger.error("文件释放锁异常");
                        }

                    })
                    ;
    }


    @PostConstruct
    public void initMethod() {
        pushMessages();

    }

    private FileLock getFileLimit(){
        for(int i=0;i<MAX_CONCURRENT_REQUESTS;i++){
            try{
                RandomAccessFile file = new RandomAccessFile(LOCK_FILE_NAME+i, "rw");
                FileChannel channel =file.getChannel();
                  FileLock fileLock =channel.tryLock();
                  if(fileLock!=null){
                      logger.info("获取文件锁名{}",LOCK_FILE_NAME+i);
                      return fileLock;
                  }
            }catch (Exception e){
                logger.error("获取文件锁失败");
            }
        }
        return null;
    }


    public FileLock getLockTry(){

        FileLock fileLock =null;
        long start = System.currentTimeMillis();//系统当前时间
        try {
            while (fileLock==null) {
                fileLock=getFileLimit();
                if (!Objects.equals(fileLock, null)) {
                    return fileLock;
                } else {
                    if (System.currentTimeMillis() - start > 2000) {
                        return null;
                    }
                }
                Thread.sleep(2000);
            }
        }catch (Exception e){
            logger.error("获取文件锁失败");
        }
        return fileLock;
    }
}
