package ru.vk.itmo.shishiginstepan;

import ru.vk.itmo.Entry;

import java.lang.foreign.MemorySegment;
import java.util.Iterator;

public class SkipDeletedIterator implements Iterator<Entry<MemorySegment>> {
    private Entry<MemorySegment> prefetched;
    private final Iterator<Entry<MemorySegment>> iterator;

    public SkipDeletedIterator(
            Iterator<Entry<MemorySegment>> iterator
    ) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        this.skipDeleted();
        return this.iterator.hasNext() || this.prefetched != null;
    }

    @Override
    public Entry<MemorySegment> next() {
        if (this.prefetched == null) {
            return this.iterator.next();
        } else {
            Entry<MemorySegment> toReturn = this.prefetched;
            this.prefetched = null;
            return toReturn;
        }
    }

    public Entry<MemorySegment> peekNext() {
        if (this.prefetched == null) {
            this.prefetched = this.iterator.next();
        }
        return this.prefetched;
    }

    public void skipDeleted() {
        while (this.iterator.hasNext()) {
            Entry<MemorySegment> next = this.peekNext();
            if (next.value() == null) {
                this.prefetched = null;
            } else break;
        }
    }
}
