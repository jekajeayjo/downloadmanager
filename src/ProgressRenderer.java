import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;


class ProgressRenderer extends JProgressBar
  implements TableCellRenderer
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

// Constructor
  public ProgressRenderer(int min, int max) {
    super(min, max);
  }

  //������� JProgressBar � �������� ������������ ��� ������ �������
  public Component getTableCellRendererComponent(
    JTable table, Object value, boolean isSelected,
    boolean hasFocus, int row, int column)
  {
    // ������� JProgressBar's ������ ����������� ����.
    setValue((int) ((Float) value).floatValue());
    return this;
  }
}