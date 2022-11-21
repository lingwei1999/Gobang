import javax.swing.Icon;
import javax.swing.JButton;

public class Blocks extends JButton{
	int place[], status;
	Blocks(int x, int y){
		super();
		int a[] = {x, y};
		place = a;
	}

	Blocks(int x, int y, Icon img){
		super(img);
		status = 0;
		int a[] = {x, y};
		place = a;
	}
	public int[] get_place(){
		return place;
	}
}