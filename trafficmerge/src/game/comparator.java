package game;

import java.util.Comparator;

import car.Car;

public class comparator implements Comparator<Car> {

	@Override
	public int compare(Car arg0, Car arg1) {
		if(arg0.meter < arg1.meter)
			return 1;
		else if(arg0.meter == arg1.meter)
			return 0;
		else
			return -1;
	}

}
