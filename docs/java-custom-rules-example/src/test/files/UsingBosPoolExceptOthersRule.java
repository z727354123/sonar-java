package kd.hrmp.hbos.formplugin.web;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UsingBosPoolExceptOthersRule {

  public void main(String[] args, String name){
    // 自定义线程池
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 3000, TimeUnit.HOURS, new LinkedBlockingQueue<>());
    // 获取已有的线程池
    ExecutorService executorService = Executors.newCachedThreadPool();
    // thread直接启动
    MyThread myThread = new MyThread();
    myThread.start(); // Noncompliant
    Thread thread = new Thread(r1);
    thread.start();// Noncompliant
    // 线程池执行
    threadPoolExecutor.execute(myThread);// Noncompliant
    threadPoolExecutor.execute(r1);// Noncompliant
    threadPoolExecutor.submit(new CallableThread());// Noncompliant
  }

  public class MyThread extends Thread {
    @Override
    public void run() {
      System.out.println(Thread.currentThread().getName() + "正在执行。。。");
    }
  }

  Runnable r1 = new Runnable(){
    public void run(){
      for(int i = 1 ; i <= 100 ; i++){
        System.out.println("*** "+i);
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  };

  public static class CallableThread implements Callable {

    @Override
    public Object call() throws Exception {
      return 5;
    }
  }
}
