import java.util.*;
import java.io.*;
import java.net.*;

//этот класс загружает файл, на который указывает УРЛ
class Download extends Observable implements Runnable {
	//max download buffer size
	private static final int MAX_BUFFER_SIZE = 1024;
	//Status names
	public static final String STATUSES[] = {"Загрузка", "Пауза", "Завершено", "Отменено", "Ошибка"};
	//Status codes
	public static final int DOWNLOADING = 0;
	public static final int PAUSED = 1;
	public static final int COMPLETE = 2;
	public static final int CANCELLED = 3;
	public static final int ERROR = 4;
	private URL url; //Загрузка адреса URL
	private int size; //размер загружаем данных в байтах
	private int downloaded; //количество загруженных байтов
	private int status; //текущее состояние процесса загрузки
	//Конструктор для класса Download
	public Download (URL url)	{
		this.url = url;
		size = -1;
		downloaded = 0;
		status = DOWNLOADING;
		//Начало процесса загрузки.
		
		download();	
	}
	//Получаем адрес URL для данного процесса загрузки
	public String getUrl() {
		return url.toString();
	}
	//Определям размер загруженных данных
	public float getSize(){
		return size;
	}
	//Получаем информацию о ходе данного процесса загрузки
	public float getProgress() {
		return ((float)downloaded/size)*100;
	}
	
	//Получаем сведения о состоянии данного процесса загрузки
	public int getStatus() {
		return status;
		}
	
	//Приостанавливаем данный процесс загрузки
	public void pause(){
		status = PAUSED;
		stateChanged();
	}
	
	//Возобновляем данный процесс загрузки
	public void resume(){
		status = DOWNLOADING;
		stateChanged();
		download();
	}
	
	//Отменяем данный процесс загрузки
	public void cancel(){
		status = CANCELLED;
		stateChanged();
	}
	
	//В процессе загрузки возникла ошибка
	private void error(){
		status = ERROR;
		stateChanged();
	}
	
	//Начинаем или возобновляем процесс загрузки
	private void download(){
		Thread thread = new Thread(this);
		thread.start();
	}
	
	//Извлекаем имя файла из адреса URL
	private String getFileName(URL url){
		String fileName = url.getFile();
		return fileName.substring(fileName.lastIndexOf('/')+1);
	}
	//загружаем файл
	public void run() {
		RandomAccessFile file = null;
		InputStream stream = null;
		//Calendar.get(Calendar.HOUR);
		try {
			//Открытие соед с адресом УРЛ
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			//Определение части файла, которую нужно загрузить
			connection.setRequestProperty("Range","bytes=" + downloaded + "-");
			//Выполняем соединение с сервером.
			connection.connect();
			//Убеждаемся в том, что код ответа находится в диапазоне 200
			if(connection.getResponseCode()/100 != 2) {
				error();
			}
			//Проверяем, имеет ли содержимое допустимую длину
			int contentLength = connection.getContentLength();
			if (contentLength < 1) {
				error();
			}
			//Задаем размер для данного процесса загрузки, если он еще не был задан
			if (size == -1) {
				size = contentLength;
				stateChanged();
			}
			//Открываем файл и ищем конец файла
			file = new RandomAccessFile(getFileName(url),"rw");
			file.seek(downloaded);
			
			stream = connection.getInputStream();
			
			while (status == DOWNLOADING) {
				//Задаем размер буфера так, чтобы загрузить оставшуюся часть файла
				byte buffer[];
				if (size - downloaded > MAX_BUFFER_SIZE){
					buffer = new byte [MAX_BUFFER_SIZE];
				} else {
					buffer = new byte [size - downloaded];
				}
				int s=0;
				//Timer Time = new Timer(1000, s++);
				//производим чтение из сервера в буфер
				int read = stream.read(buffer);
				if (read == -1)
					break;
				//запісываем содержимое буфера в файл
				file.write(buffer,0,read);
				downloaded+=read;
				stateChanged();
			}
			//Определяем состояние как завершенное
			if (status==DOWNLOADING){
				status = COMPLETE;
				stateChanged();
			}
		}catch (Exception e) {
			error();
		} finally {
			//Close file
			if (file !=null){
				try {
					file.close();
				} catch (Exception e) {}
			}
		}
	}
	//Уведомляем наблюдателей об изменении состояния данного процесса загрузки
	private void stateChanged(){
		setChanged();
		notifyObservers();
	}
}