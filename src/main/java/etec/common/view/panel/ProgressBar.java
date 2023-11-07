package etec.common.view.panel;

import javax.swing.JProgressBar;

public class ProgressBar extends JProgressBar{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int total;
	
	private int progress;
	
	public ProgressBar(){
		setStringPainted(true);
	}
	
	public void setUnit(int total) {
		this.total = total;
	}
	
	public void reset() {
		progress=0;
		setValue(progress);
	}
	
	public void plusOne() {
		progress++;
		setValue(progress * 100 / total);
	}
	public int getProgress() {
		return progress;
	}
	public int getTotal() {
		return total;
	}
}
