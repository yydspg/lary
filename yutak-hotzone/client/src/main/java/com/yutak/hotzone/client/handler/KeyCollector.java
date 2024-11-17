package com.yutak.hotzone.client.handler;

import com.yutak.hotzone.entry.YutakEntry;

import java.util.List;

public interface KeyCollector {

   /**
    *read collect key
    */
   List<YutakEntry>  exec();
   /*
   send key
    */
   void put(YutakEntry entry);
}
