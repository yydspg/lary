// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.metric;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MetricTest {

    @Test
    void eventCarriesAttributesAndMetadata() {
        Event event = new Event("dag", "node.success", "fetch")
                .attr("type", "FetchNode")
                .attrNanos("elapsed_ns", 1200)
                .durationNanos(1200)
                .ok(false);

        assertEquals("dag", event.source());
        assertEquals("node.success", event.action());
        assertEquals("fetch", event.subject());
        assertEquals("FetchNode", event.get("type").orElseThrow());
        assertEquals("1200", event.get("elapsed_ns").orElseThrow());
        assertEquals(1200, event.durationNanos());
        assertFalse(event.ok());
    }

    @Test
    void statsMonitorAggregatesByAction() {
        StatsMonitor monitor = new StatsMonitor();
        monitor.emit(new Event("dag", "node.start", "a"));
        monitor.emit(new Event("dag", "node.success", "a").durationNanos(5));
        monitor.emit(new Event("dag", "node.failure", "b").ok(false).durationNanos(7));

        assertEquals(3, monitor.eventCount());
        assertEquals(1, monitor.errorCount());
        assertEquals(1, monitor.count("node.start"));
        assertEquals(1, monitor.count("node.success"));
        assertEquals(5, monitor.durationNanos("node.success"));
        assertTrue(monitor.summary().contains("events=3"));
    }

    @Test
    void compositeFansOutAndFactoryCreatesBuiltIns() {
        StatsMonitor stats = new StatsMonitor();
        CompositeMonitor composite = new CompositeMonitor().add(stats);
        composite.emit(new Event("dag", "op.end", "x"));
        assertEquals(1, stats.eventCount());
        assertEquals(1, composite.size());

        MonitorFactory factory = MonitorFactory.instance();
        assertTrue(factory.contains("stats"));
        assertSame(CompositeMonitor.class, factory.create("composite").getClass());
    }
}
