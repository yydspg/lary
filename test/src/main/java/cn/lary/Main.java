package cn.lary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Main.class);
        System.out.println(logger);
        logger.debug("what can i say");
        System.out.println("Hello world!");
    }
}