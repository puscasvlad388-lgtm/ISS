package com.bitewise.dto;

import com.bitewise.domain.UnitType;

/** Element din lista de cumparaturi generata dintr-o reteta. */
public class ShoppingListItem {
    private String ingredientName;
    private double neededQuantity;
    private double inPantry;
    private double toBuy;
    private UnitType unit;

    public ShoppingListItem() {}

    public ShoppingListItem(String ingredientName, double neededQuantity, double inPantry, double toBuy, UnitType unit) {
        this.ingredientName = ingredientName;
        this.neededQuantity = neededQuantity;
        this.inPantry = inPantry;
        this.toBuy = toBuy;
        this.unit = unit;
    }

    public String getIngredientName() { return ingredientName; }
    public void setIngredientName(String ingredientName) { this.ingredientName = ingredientName; }
    public double getNeededQuantity() { return neededQuantity; }
    public void setNeededQuantity(double neededQuantity) { this.neededQuantity = neededQuantity; }
    public double getInPantry() { return inPantry; }
    public void setInPantry(double inPantry) { this.inPantry = inPantry; }
    public double getToBuy() { return toBuy; }
    public void setToBuy(double toBuy) { this.toBuy = toBuy; }
    public UnitType getUnit() { return unit; }
    public void setUnit(UnitType unit) { this.unit = unit; }
}
