package main.java.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class Statistics {
    private long allowedTotal = 0;
    private long deniedTotal = 0;
    private long unreadableTotal = 0;
    private final EnumMap<CardType, Long> allowedByType = new EnumMap<>(CardType.class);
    private final EnumMap<CardType, Long> deniedByType = new EnumMap<>(CardType.class);

    public  Statistics() {
        for (CardType cardType : CardType.values()) {
            allowedByType.put(cardType, 0L);
            deniedByType.put(cardType, 0L);
        }
    }

    public void record(CardType cardType, PassResult result) {
        if (result == PassResult.ALLOWED) {
            allowedByType.merge(cardType, 1L, Long::sum);
            allowedTotal++;
        } else {
            deniedByType.merge(cardType, 1L, Long::sum);
            deniedTotal++;
        }
    }

    public void recordUnreadable() {
        unreadableTotal++;
        deniedTotal++;
    }


    public  Map<CardType, Long> getAllowedByType() {
        return Collections.unmodifiableMap(allowedByType);
    }

    public Map<CardType, Long> getDeniedByType() {
        return Collections.unmodifiableMap(deniedByType);
    }

    public long getUnreadableTotal() {
        return unreadableTotal;
    }

    public long getAllowedTotal() {
        return allowedTotal;
    }

    public long getDeniedTotal() {
        return deniedTotal;
    }

    public long getTotal() {
        return allowedTotal + deniedTotal;
    }
}
