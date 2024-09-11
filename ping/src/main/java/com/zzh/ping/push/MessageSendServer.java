package com.zzh.ping.push;

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

    public void pushMessages() {
        Flux.interval(Duration.ofSeconds(1)) // 一秒发送一条信息
                .map(tick -> "hello")
                .flatMap(t->{
                    //获取锁
                    FileLock fileLock=getLockTry();

                    if (fileLock!=null) {
                        return pongPushInit(t,fileLock);
                    }else {
                        logger.error("Speed limited");
                        return Mono.just("Request not sent");
                    }
                }) // 发送信息至pong端
                .subscribe(response ->{
                                logger.info("接收到pong服务的响应消息为{}",response);
                        } ,
                        error -> logger.error("请求错误"));
    }



    public Mono<String> pongPushInit(String data,FileLock fileLock) {
            return webClient.post()
                    .uri("/receive")//请求路径
                    .contentType(MediaType.TEXT_PLAIN)
                    .bodyValue(data)//请求参数
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorResume(error -> {
                        return Mono.just("pong service Error ===========");
                    })
                    .doFinally(unLock->{
                        try {
                            logger.info("获取文件锁为======{}",fileLock);
                            fileLock.release();
                            logger.info("释放锁成功=======");
                        }catch (Exception e){
                            logger.error("文件释放锁异常Error===========");
                        }

                    })
                    ;
    }


    @PostConstruct
    public void initMethod() {
        pushMessages();

    }

    private FileLock getFileLimit(){
        for(int i=0;i<2;i++){
            try{
                RandomAccessFile file = new RandomAccessFile("ping_lockup"+i, "rw");
                FileChannel channel =file.getChannel();
                  FileLock fileLock =channel.tryLock();
                  if(fileLock!=null){
                      logger.info("获取文件锁名========{}","ping_lockup"+i);
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
