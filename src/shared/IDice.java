package shared;

import java.io.Serializable;

public interface IDice extends Serializable {

	int roll();

	int roll(int num);
}
