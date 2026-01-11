import sss.dpbuilder_1.item.burger.ChickenBurger;
import sss.dpbuilder_1.item.burger.VegBurger;
import sss.dpbuilder_1.item.colddrink.Coke;
import sss.dpbuilder_1.item.colddrink.Pepsi;

public class MealBuilder {

   public MealOrdered prepareVegMeal (){
      MealOrdered MealOrdered = new MealOrdered();
      MealOrdered.addItem(new VegBurger());
      MealOrdered.addItem(new Coke());
      return MealOrdered;
   }   

   public MealOrdered prepareNonVegMeal (){
      MealOrdered MealOrdered = new MealOrdered();
      MealOrdered.addItem(new ChickenBurger());
      MealOrdered.addItem(new Pepsi());
      return MealOrdered;
   }
}
