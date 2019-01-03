package io.lylix.lya.recipe;

import io.lylix.lya.LYAConfig;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.function.BooleanSupplier;

public class LYARecipeEnabledCondition implements IConditionFactory
{
    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json)
    {
        if(JsonUtils.hasField(json, "addon"))
        {
            return () -> LYAConfig.isEnable(JsonUtils.getString(json, "addon"));
        }

        throw new IllegalStateException("Config defined with recipe_enabled condition without a valid field defined!");
    }
}
