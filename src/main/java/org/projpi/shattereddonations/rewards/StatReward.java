package org.projpi.shattereddonations.rewards;

import org.bukkit.entity.Player;
import org.projpi.shattereddonations.ShatteredDonations;

import java.util.Arrays;
import java.util.Map;

public class StatReward implements DonationReward
{
    public static final RewardParser parser = new Parser();

    private final StatType statType;
    private final StatOperation operation;
    private final int change;
    private final String name;

    public StatReward(StatType statType, StatOperation operation, int change, String name)
    {
        this.statType = statType;
        this.operation = operation;
        this.change = change;
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void execute(Player player)
    {
        switch (statType)
        {
            case HEALTH:
                player.setHealth(operation == StatOperation.ABSOLUTE
                        ? (int) Math.max(0, Math.min(change, 20))
                        : (int) Math.max(0, Math.min(player.getHealth() + change, 20)));
                break;
            case HUNGER:
                player.setFoodLevel(operation == StatOperation.ABSOLUTE
                        ? (int) Math.max(0, Math.min(change, 20))
                        : (int) Math.max(0, Math.min(player.getFoodLevel() + change, 20)));
                break;
        }
    }


    private static class Parser implements RewardParser
    {
        @Override
        public DonationReward parse(ShatteredDonations instance, Map map)
        {
            String stat = (String) map.get("stat");
            if(stat == null)
            {
                instance.getLogger().warning("Stat reward attempted to load but was missing required field " +
                        "'stat'. It has been skipped.");
                return null;
            }
            StatType type = StatType.fromString(stat);
            if(type == null)
            {
                instance.getLogger().warning("Stat reward attempted to load but was given invalid field 'stat'." +
                        "Valid values are: " + Arrays.asList(StatType.values()).toString() + ". It has been skipped.");
                return null;
            }
            Integer amount = (Integer) map.get("value");
            if(amount == null)
            {
                instance.getLogger().warning("Stat reward attempted to load but was missing required field " +
                        "'amount'. It has been skipped.");
                return null;
            }
            StatOperation operation = map.containsKey("operation")
                    ? StatOperation.valueOf((String) map.get("operation"))
                    : StatOperation.RELATIVE;

            String name = map.containsKey("name")
                    ? (String) map.get("name")
                    : type.toString();

            return new StatReward(type, operation, amount, name);
        }

        @Override
        public String getType()
        {
            return "stat";
        }
    }

    private enum StatType
    {
        HEALTH("Health"),
        HUNGER("Hunger");

        private final String text;

        StatType(String text)
        {
            this.text = text;
        }

        public String getText()
        {
            return text;
        }

        public static StatType fromString(String text) {
            for (StatType b : StatType.values()) {
                if (b.text.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    private enum  StatOperation {
        RELATIVE,
        ABSOLUTE
    }
}
