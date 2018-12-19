import java.math.BigDecimal;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;


class DownloadsTableModel extends AbstractTableModel
  implements Observer
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

// Имена столбцов
  private static final String[] columnNames = {"URL", "Размер",
    "Процесс", "Статус", "Время"};

  // Классы для каждого столбца
  @SuppressWarnings("rawtypes")
  private static final Class[] columnClasses = {String.class,
    String.class, JProgressBar.class, String.class, String.class};

  // Список загрузок
  private ArrayList<Download> downloadList =
             new ArrayList<Download>();

  // Добавление новой загрузки
  public void addDownload(Download download) {
    // Регистрация для получения изменений загрузок
    download.addObserver(this);

    downloadList.add(download);

    // Создание для таблицы уведомления о смене строки
    fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
  }

  // Получение процесса загрузки для определенной строки.
  public Download getDownload(int row) {
    return (Download) downloadList.get(row);
  }

  // Удаление загрузки из строки.
  public void clearDownload(int row) {
    downloadList.remove(row);

    // Создание для таблицы уведомления об уведомлении строки
    fireTableRowsDeleted(row, row);
  }

  // Получение подсчитанного кол-ва строк таблицы
  public int getColumnCount() {
    return columnNames.length;
  }

  // Получение имени столбца
  public String getColumnName(int col) {
     return columnNames[col];
  }

  // Получение класса для столбца
  public Class<?> getColumnClass(int col) {
    return columnClasses[col];
  }

  // Получение числа строк
  public int getRowCount() {
    return downloadList.size();
  }

  // Получение значения для комбинации строки и столбца.
  public Object getValueAt(int row, int col) {
    Download download = downloadList.get(row);
    switch (col) {
      case 0: // URL
        return download.getUrl();
      case 1: // Size
        float size = download.getSize()/(1024*1024);
        return (size == -1) ? "" : Float.toString(size);
      case 2: // Progress
        return new Float(download.getProgress());
      case 3: // Status
        return Download.STATUSES[download.getStatus()];
    }
    return "";
  }

  //Обновление при каком-либо изменении 
  public void update(Observable o, Object arg) {
    int index = downloadList.indexOf(o);
    // Создание для таблицы уведомления об обновлении строки
    fireTableRowsUpdated(index, index);
  }
}