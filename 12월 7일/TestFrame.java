import java.awt.Dimension;
import javax.swing.JFrame;

public class TestFrame {
	public static void main(String[] args) {
		JFrame frame = new JFrame("TESTMODE");
		frame.setPreferredSize(new Dimension(1200, 900));
		
		AppManager app = AppManager.getS_instance();
		
		ChartPrimaryPanel primary = AppManager.getS_instance().getPnlChartPrimary();
		frame.getContentPane().add(primary);
		
		frame.pack();
		frame.setVisible(true);
	}
	
}