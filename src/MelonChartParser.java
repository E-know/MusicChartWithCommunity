import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.awt.*;

/**
 * 
 * @author SejongUniv ��â��
 * @version 1.7
 *
 **/

public class MelonChartParser extends MusicChartParser{

	/*
	 * MelonChartParser Description (KO_KR)
	 * 
	 **************************************************
	 * 
	 * ** ��Ʈ 100���� �Ľ��� �ÿ� ���� �� �ִ� �͵� **
	 * �뷡 ���̵�		(key : songId)
	 * �뷡 ����		(key : rank)
	 * �뷡 ���� �̹���	(key : smallImageUrl)
	 * �뷡 ����		(key : title)
	 * ���� �̸�		(key : artist)
	 * �ٹ� �̸�		(key : albumName)
	 * �뷡 ���ƿ� ����	(key : likeNum)
	 * 
	 * ** ��Ʈ 100���� �Ľ��� �ÿ� ��� ������ �޼ҵ� **
	 * - �޼ҵ� �̸��� ���� �޼ҵ���� ��ȯ���� ��� ����.
	 * - [��ȯ��] �޼ҵ��̸�() �� ���� ǥ���ߴ�.
	 * 
	 * <��Ʈ 100�� �Ľ� ���� �޼ҵ�>
	 * [void]		chartDataParsing(Component parentComponent)
	 * [boolean]	isParsed()
	 * 
	 * <��Ʈ 100�� �뷡 ���� get �޼ҵ�>
	 * [JSONArray]	getChartList()
	 * [JSONObject]	getSongData(int rank)	getSongData(String title)
	 * [int]		getRank(String title)	getRank(JSONObject jObj)
	 * [String]		getTitle(int rank)		getTitle(JSONObject jObj)
	 * [String]		getArtistName(int rank)	getArtistName(String title)		getArtistName(JSONObject jObj)
	 * [String]		getAlbumName(int rank)	getAlbumName(String title)		getAlbumName(JSONObject jObj)
	 * [String]		getLikeNum(int rank)	getLikeNum(String title)		getLikeNum(JSONObject jObj)
	 * [String]		getImageUrl(int rank)	getImageUrl(String title)		getImageUrl(JSONObject jObj)
	 * [String]		getSongId(int rank)		getSongId(String title)			getSongId(JSONObject jObj)
	 *
	 **************************************************
	 *
	 * ** �뷡 1���� ���� �� ������ �Ľ��� �ÿ� ���� �� �ִ� �͵� **
	 * �뷡 ū �̹���		(key : imageUrl)
	 * �뷡 �߸���		(key : releaseDate)
	 * �뷡 �帣		(key : genre)
	 *
	 * ** �뷡 1���� ���� �� ������ �Ľ��� �ÿ� ��� ������ �޼ҵ� **
	 * - �޼ҵ� �̸��� ���� �޼ҵ���� ��ȯ���� ��� ����.
	 * - [��ȯ��] �޼ҵ��̸�() �� ���� ǥ���ߴ�.
	 * 
	 * <�뷡 1���� ���� �� ���� �Ľ� ���� �޼ҵ�>
	 * [void]		songDetailDataParsing(String songId, Component parentComponent)
	 * [void]		songDetailDataParsing(JSONObject jObj, Component parentComponent)
	 * [void]		songDetailDataParsing(int rank, JSONArray chartListData, Component parentComponent)
	 * [void]		songDetailDataParsing(String title, JSONArray chartListData, Component parentComponent)
	 * [boolean]	isParsed()
	 * 
	 * <�뷡 1���� ���� �� ���� get �޼ҵ�>
	 * [JSONObject]	getSongData()
	 * [String]		getImageUrl()		getImageUrl(JSONObject jObj)	getImageUrl(int rank)	getImageUrl(String title)
	 * [String]		getReleaseDate()	getReleaseDate(JSONObject jObj)
	 * [String]		getGenre()			getGenre(JSONObject jObj)
	 * 
	 **************************************************
	 *
	 */
	
	private String melonChartParsingTitle = "��� ��Ʈ �Ľ���..";
	private String melonChartParsingMessage = "��� ��Ʈ 100� ���� ������ �ҷ����� �� �Դϴ� :)";
	
	public MelonChartParser() { // �ʱ�ȭ �۾��� ������
		songCount = 0;				// �Ľ��� �뷡 ����(�ʱⰪ�� 0)
		chartList = null;			// ��Ʈ 100� ���� ������ ���� JSONArray
		songDetailInfo = null;		// �뷡 �� � ���� �� ������ ���� JSONObject
		url = null;					// �Ľ��� �� ����Ʈ url
		chartThread = null;			// ��Ʈ 100�� �Ľ̿� ����� Thread
		songDetailThread = null;	// �뷡 �� � ���� �� ���� �Ľ̿� ����� Thread
		progressMonitor = null; 	// ProgressMonitor�� ����ϸ� Thread�� ������� �ʴ� ���׿� ProgressMonitor�� ����� ������ �ʴ� ���װ� �߻��Ͽ� ����ϴ� �κ��� �ּ�ó�� �صξ���
	} // constructor
	
	private class ChartDataParsingThread implements Runnable { // ��Ʈ 100�� �Ľ��� �ϴ� Runnable class
		@Override
		public void run() {
			// ��� ��Ʈ 1~100���� �뷡�� �Ľ���
			songCount = 0; // �뷡 ���� �ʱ�ȭ
			url = "https://www.melon.com/chart/index.htm"; // �Ľ��� url

			try {
				// ��� ��Ʈ ���ῡ �ʿ��� header ���� �� ����
				Connection melonConnection = Jsoup.connect(url).header("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
						.header("Sec-Fetch-User", "?1").header("Upgrade-Insecure-Requests", "1")
						.header("User-Agent",
								"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36")
						.method(Connection.Method.GET);

				// ���� �� ���������� �ܾ��
				Document melonDocument = melonConnection.get();

				// 1~50���� ���� ������ �ҷ���, ������ ���� ���� ������ �̱� ���� ��ũ�� �̴� �뵵�� ���
				Elements data1st50 = melonDocument.select("tr.lst50");
				
				// 51~100���� ���� ������ �ҷ���, ������ ���� ���� ������ �̱� ���� ��ũ�� �̴� �뵵�� ���
				Elements data51st100 = melonDocument.select("tr.lst100");

				chartList = new JSONArray();

				for (Element elem : data1st50) { // 1~50���� ���� ���� �Ľ�
					// JSONObject�� �����͸� �ֱ� ���� �۾�
					HashMap<String, Object> songAllInfo = new HashMap<String, Object>();

					// key : songId, value : �뷡 ���̵� - �� ������ �Ľ��� �� �ʿ���
					songAllInfo.put("songId", elem.attr("data-song-no").toString());

					// key : rank, value : ����
					songAllInfo.put("rank", elem.select("span.rank").first().text());

					// key : smallImageUrl, value : �뷡 �̹���(������ ����) ��ũ (ū ������ �̹����� detailDataParsing���� �ٷ�)
					songAllInfo.put("smallImageUrl", elem.select("a > img").first().attr("src").toString());

					// key : title, value : �뷡 ����
					songAllInfo.put("title", elem.select("div.ellipsis > span > a").first().text().toString());

					// key : artist, value : ���� �̸�
					songAllInfo.put("artist", elem.select("div.ellipsis").get(1).select("a").first().text().toString());

					// key : albumName, value : �ٹ� �̸�
					songAllInfo.put("albumName", elem.select("div.ellipsis").get(2).select("a").text().toString());

					// �뷡�� ���ƿ� ������ �̾Ƴ��� ���� url�� ����, ����� ���ƿ� ������ ���� �ҷ����� ����̶� �ܼ� ũ�Ѹ����δ� �ҷ������� �ʱ� ����
					String likeNumUrl = "https://www.melon.com/commonlike/getSongLike.json?contsIds="
							+ songAllInfo.get("songId").toString();

					// �뷡�� ���ƿ� ������ �̾Ƴ��� ���� url�� ���� �� JSON�� �޾ƿ�
					Document likeNumDocument = Jsoup.connect(likeNumUrl).header("Accept",
							"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
							.header("Sec-Fetch-User", "?1").header("Upgrade-Insecure-Requests", "1")
							.header("User-Agent",
									"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36")
							.ignoreContentType(true).get();

					// JSONParser�� �Ľ��Ͽ� JSONObject�� ��ȯ�ϰ�, HashMap�� �߰���
					// key : likeNum, value : ���ƿ� ����
					JSONParser parser = new JSONParser();
					JSONObject obj = (JSONObject) parser.parse(likeNumDocument.text());
					songAllInfo.put("likeNum",
							((JSONObject) (((JSONArray) obj.get("contsLike")).get(0))).get("SUMMCNT").toString());

					// ������ JSONObject�� ��ȯ
					JSONObject jsonSongInfo = new JSONObject(songAllInfo);

					// JSONArray�� �� �߰�, �뷡 ���� ����
					chartList.add(jsonSongInfo);
					songCount++;
					//progressMonitor.setProgress(songCount);
				}
			
				for (Element elem : data51st100) { // 51~100���� ���� ���� �Ľ�
					// JSONObject�� �����͸� �ֱ� ���� �۾�
					HashMap<String, Object> songAllInfo = new HashMap<String, Object>();

					// key : songId, value : �뷡 ���̵� - �� ������ �Ľ��� �� �ʿ���
					songAllInfo.put("songId", elem.attr("data-song-no").toString());

					// key : rank, value : ����
					songAllInfo.put("rank", elem.select("span.rank").first().text());

					// key : smallImageUrl, value : �뷡 �̹���(������ ����) ��ũ (ū ������ �̹�����
					// detailDataParsing���� �ٷ�)
					songAllInfo.put("smallImageUrl", elem.select("a > img").first().attr("src").toString());

					// key : title, value : �뷡 ����
					songAllInfo.put("title", elem.select("div.ellipsis > span > a").first().text().toString());

					// key : artist, value : ���� �̸�
					songAllInfo.put("artist", elem.select("div.ellipsis").get(1).select("a").first().text().toString());

					// key : albumName, value : �ٹ� �̸�
					songAllInfo.put("albumName", elem.select("div.ellipsis").get(2).select("a").text().toString());

					// �뷡�� ���ƿ� ������ �̾Ƴ��� ���� url�� ����, ����� ���ƿ� ������ ���� �ҷ����� ����̶� �ܼ� ũ�Ѹ����δ� �ҷ������� �ʱ� ����
					String likeNumUrl = "https://www.melon.com/commonlike/getSongLike.json?contsIds=" + songAllInfo.get("songId").toString();

					// �뷡�� ���ƿ� ������ �̾Ƴ��� ���� url�� ���� �� JSON�� �޾ƿ�
					Document likeNumDocument = Jsoup.connect(likeNumUrl).header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
							.header("Sec-Fetch-User", "?1")
							.header("Upgrade-Insecure-Requests", "1")
							.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36")
							.ignoreContentType(true).get();

					// JSONParser�� �Ľ��Ͽ� JSONObject�� ��ȯ�ϰ�, HashMap�� �߰���
					// key : likeNum, value : ���ƿ� ����
					JSONParser parser = new JSONParser();
					JSONObject obj = (JSONObject) parser.parse(likeNumDocument.text());
					songAllInfo.put("likeNum", ((JSONObject) (((JSONArray) obj.get("contsLike")).get(0))).get("SUMMCNT").toString());

					// ������ JSONObject�� ��ȯ
					JSONObject jsonSongInfo = new JSONObject(songAllInfo);

					// JSONArray�� �� �߰�, �뷡 ���� ����
					chartList.add(jsonSongInfo);
					songCount++;
					
					//progressMonitor.setProgress(songCount);
				}

				
				// �Ľ� ��� ���(�׽�Ʈ��)
				/*
				for (Object o : chartList) {
					if (o instanceof JSONObject)
						System.out.println(((JSONObject) o));
				}
				*/
			} // try
			catch (HttpStatusException e) { // ����� ��� Request Header�� ���� �����־ �ʹ� ���� �Ľ��� �õ��� �ÿ� �Ͻ��� ������ �ϹǷ� �׿� ���� ó��
				e.printStackTrace();
				chartList = null;
				songDetailInfo = null;
				System.out.println("���� ��û���� ���� �ҷ����⿡ �����Ͽ����ϴ�.");
				songCount = 0;
				return;
			}
			catch (NullPointerException e) { // ������ �ܾ���� ���� �������� ��(�±׳� �Ӽ��� ���� ��)
				e.printStackTrace();
				chartList = null;
				songDetailInfo = null;
				System.out.println("Url ��ũ�� �߸��Ǿ��ų�, �� ������ ������ ����Ǿ� �Ľ̿� �����߽��ϴ� :(");
				songCount = 0;
				return;
			}
			catch (Exception e) { // �� ���� ��� ����
				e.printStackTrace();
				chartList = null;
				songDetailInfo = null;
				System.out.println("�Ľ̵��� ������ �߻��߽��ϴ� :(");
				songCount = 0;
				return;
			}
		} // run()
	} // ChartDataParsingThread Runnable class
	
	private class SongDetailDataParsingThread implements Runnable { // �뷡 �� � ���� �� �Ľ��� �ϴ� Runnable class
		@Override
		public void run() {
			// �뷡 �� � ���� �� ���� �Ľ�
			songCount = 0; // �뷡 ���� �ʱ�ȭ
			HashMap<String, Object> songAllInfo = new HashMap<String, Object>();
			
			try {
				// songId�� ���� � ���� ���� ������ ��� ���� ����
				Connection songDetailConnection = Jsoup.connect(url).header("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
						.header("Sec-Fetch-User", "?1").header("Upgrade-Insecure-Requests", "1")
						.header("User-Agent",
								"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36")
						.method(Connection.Method.GET);

				// � ���� ���� ���� �� �������� �ܾ��
				Document songDetailDocument = songDetailConnection.get();
				
				Element songDetailInfo = songDetailDocument.select(".wrap_info").first();

				// key : imageUrl, value : �뷡 �̹��� ��ũ(�̹��� ����� ŭ)
				String songImageUrl = songDetailInfo.getElementsByTag("img").first().attr("src");
				songAllInfo.put("imageUrl", songImageUrl);

				Element songDetailEtcInfo = songDetailInfo.select("dl.list").first();
				
				// key : releaseDate, value : �뷡 �߸���
				String songReleaseDate = songDetailEtcInfo.getElementsByTag("dd").get(1).text();
				songAllInfo.put("releaseDate", songReleaseDate);
				
				// key : genre, value : �뷡 �帣
				String songGenre = songDetailEtcInfo.getElementsByTag("dd").get(2).text();
				songAllInfo.put("genre", songGenre);
			} // try
			catch (HttpStatusException e) { // ����� ��� Request Header�� ���� �����־ �ʹ� ���� �Ľ��� �õ��� �ÿ� �Ͻ��� ������ �ϹǷ� �׿� ���� ó��
				e.printStackTrace();
				chartList = null;
				songDetailInfo = null;
				System.out.println("���� ��û���� ���� �ҷ����⿡ �����Ͽ����ϴ�.");
				songCount = 0;
				return;
			}
			catch (NullPointerException e) { // ������ �ܾ���� ���� �������� ��(�±׳� �Ӽ��� ���� ��)
				e.printStackTrace();
				chartList = null;
				songDetailInfo = null;
				System.out.println("Url ��ũ�� �߸��Ǿ��ų�, �� ������ ������ ����Ǿ� �Ľ̿� �����߽��ϴ� :(");
				songCount = 0;
				return;
			}
			catch (Exception e) { // �� ���� ��� ����
				e.printStackTrace();
				chartList = null;
				songDetailInfo = null;
				System.out.println("�Ľ̵��� ������ �߻��߽��ϴ� :(");
				songCount = 0;
				return;
			}
			songDetailInfo = new JSONObject(songAllInfo); // HashMap�� JSONObject�� ��ȯ�Ͽ� ����
			songCount++; // �뷡 ���� ����
		} // run()
	} // SongDetailDataParsingThread Runnable class
	
	@Override
	public void chartDataParsing(Component parentComponent) { // ��Ʈ 100���� �Ľ��ϴ� Thread�� �����ϴ� �޼ҵ�
		if (chartThread != null) { // Thread�� ����ϴ°� ó���� �ƴ� ��
			if (chartThread.isAlive()) { // Thread�� ��������� ����
				chartThread.stop();
				System.out.println("Chart Thread is Alive");
			}
			else
				System.out.println("Chart Thread is dead");
		}
		chartThread = new Thread(new ChartDataParsingThread()); // Thread�� ������ �ȵǱ� ������ �ٽ� ��ü�� ������
		// progressMonitorManager(parentComponent, melonChartParsingTitle, melonChartParsingMessage);
		chartThread.start(); // Thread ����
		try {
			chartThread.join(); // ChartDataParsingThread�� ����Ǳ������� ���
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	} // chartDataParsing(Component parentComponent)

	@Override
	public void songDetailDataParsing(String songId, Component parentComponent) { // �뷡 �� � ���� �� ������ �Ľ��ϴ� Thread�� �����ϴ� �޼ҵ�
		url = "https://www.melon.com/song/detail.htm?songId=" + songId; // �Ľ��� url�� ����
		if (songDetailThread != null) { // Thread�� ����ϴ� �� ó���� �ƴ� ��
			if (songDetailThread.isAlive()) // Thread�� ��������� ����
				songDetailThread.stop();
		}
		songDetailThread = new Thread(new SongDetailDataParsingThread()); // Thread�� ������ �ȵǱ� ������ �ٽ� ��ü�� ������
		// progressMonitorManager�� ��������
		songDetailThread.start(); // Thread ����
		try {
			songDetailThread.join(); // SongDetailDataParsingThread�� ����Ǳ� ������ ���
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	} // songDetailDataParsing(String songId, Component parentComponent)

	@Override
	public void songDetailDataParsing(JSONObject jObj, Component parentComponent) { // �뷡 �� � ���� �� ������ �Ľ��ϴ� Thread�� �����ϴ� �޼ҵ�
		if (jObj == null) {
			System.out.println(plzUseRightJSONObject);
			return;
		}
		
		if (!jObj.containsKey("songId")) { // songId key�� ��ȿ�� �˻�
			System.out.println(jsonDontHaveKey);
			return;
		}
		url = "https://www.melon.com/song/detail.htm?songId=" + jObj.get("songId").toString(); // �Ľ��� url�� ����
		if (songDetailThread != null) { // Thread�� ����ϴ� �� ó���� �ƴ� ��
			if (songDetailThread.isAlive()) // Thread�� ��������� ����
				songDetailThread.stop();
		}
		songDetailThread = new Thread(new SongDetailDataParsingThread()); // Thread�� ������ �ȵǱ� ������ �ٽ� ��ü�� ������
		songDetailThread.start();
		try {
			songDetailThread.join(); // SongDetailDataParsingThread�� ����Ǳ� ������ ���
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	} // songDetailDataParsing(JSONObject jObj, Component parentComponent)
	
	@Override
	public void songDetailDataParsing(int rank, JSONArray chartListData, Component parentComponent) { // �뷡 �� � ���� �� ������ �Ľ��ϴ� Thread�� �����ϴ� �޼ҵ�
		if (chartListData == null) {
			System.out.println("��Ʈ �Ľ̵� �����Ͱ� ���� �޼ҵ� ������ �����մϴ� :(");
			return;
		}
		url = "https://www.melon.com/song/detail.htm?songId="
				+ ((JSONObject) chartListData.get(rank - 1)).get("songId").toString(); // �Ľ��� url�� ����
		
		if (songDetailThread != null) { // Thread�� ����ϴ� �� ó���� �ƴ� ��
			if (songDetailThread.isAlive()) // Thread�� ��������� ����
				songDetailThread.stop();
		}
		songDetailThread = new Thread(new SongDetailDataParsingThread()); // Thread�� ������ �ȵǱ� ������ �ٽ� ��ü�� ������
		songDetailThread.start();
		try {
			songDetailThread.join(); // SongDetailDataParsingThread�� ����Ǳ� ������ ���
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	} // songDetailDataParsing(int rank, JSONArray chartListData, Component parentComponent)
	
	@Override
	public void songDetailDataParsing(String title, JSONArray chartListData, Component parentComponent) { // �뷡 �� � ���� �� ������ �Ľ��ϴ� Thread�� �����ϴ� �޼ҵ�
		/* ����õ �ϴ� �޼ҵ� �Դϴ�. title�� �´� �����͸� ó������ ã�ư��� �ϱ� ������ �� �� ��ȿ�����Դϴ�. */
		String tmpSongId = null;
		
		if (chartListData == null) {
			System.out.println("��Ʈ �Ľ̵� �����Ͱ� ���� �޼ҵ� ������ �����մϴ� :(");
			return;
		}

		for (int i = 0; i < 100; i++) { // ��Ʈ 100���� �����Ϳ��� title�� �´� �����͸� ã�� songId ���� �Ľ��� url�� ����
			if (((JSONObject) chartListData.get(i)).get("title").toString() == title) {
				url = "https://www.melon.com/song/detail.htm?songId=" + ((JSONObject) chartListData.get(i)).get("songId").toString();
				tmpSongId = ((JSONObject) chartListData.get(i)).get("songId").toString();
				break;
			}
		}
		if (tmpSongId == null) {
			System.out.println("���� �ش��ϴ� �뷡�� ��Ʈ �����Ϳ� ���� �ҷ��� �� �����ϴ� :(");
			return;
		}
		else {
			if (songDetailThread != null) { // Thread�� ����ϴ� �� ó���� �ƴ� ��
				if (songDetailThread.isAlive()) // Thread�� ��������� ����
					songDetailThread.stop();
			}
			songDetailThread = new Thread(new SongDetailDataParsingThread()); // Thread�� ������ �ȵǱ� ������ �ٽ� ��ü�� ������
			songDetailThread.start();
			try {
				songDetailThread.join(); // SongDetailDataParsingThread�� ����Ǳ� ������ ���
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	} // songDetailDataParsing(String title, JSONArray chartListData, Component parentComponent)
	
	// chartDataParsing �Ŀ��� ��밡���� �޼ҵ�
	public String getLikeNum(int rank) { // �뷡 ������ �̿��Ͽ� �ش� �뷡�� ���ƿ� ������ ��ȯ�ϴ� �޼ҵ�
		if (rank < 1 || rank > 100) { // 1 <= rank <= 100�� ����� �������
			System.out.println("1~100�� �̳��� ������ �Է����ּ���");
			return null;
		}
		
		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}
		
		if (songCount == 1) { // �� �Ľ��� �̷�����ٸ�
			System.out.println("getLikeNum(int rank) : " + isOnlyChartParse);
			return null;
		}
		
		return ((JSONObject)chartList.get(rank - 1)).get("likeNum").toString();
	} // String getLikeNum(int rank)

	//chartDataParsing �Ŀ��� ��밡���� �޼ҵ�
	public String getLikeNum(String title) { // �뷡 ������ �̿��Ͽ� �ش� �뷡�� ���ƿ� ������ ��ȯ�ϴ� �޼ҵ�
		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}
		
		if (songCount == 1) { // �� �Ľ��� �̷�����ٸ�
			System.out.println("getLikeNum(String title) : " + isOnlyChartParse);
			return null;
		}

		for (int i = 0; i < songCount; i++) { // ��Ʈ 100� ���� �Ľ��� �̷�����ٸ� JSONArray���� �뷡 ���� �´� ���Ҹ� ã�� �ش� �뷡�� ���ƿ� ������ ��ȯ�ϴ� �Լ�
			if (((JSONObject) chartList.get(i)).get("title") == title)
				return ((JSONObject) chartList.get(i)).get("likeNum").toString();
		}
		
		return null;
	} // String getLikeNum(String title)

	// songDetailDataParsing �Ŀ��� ��밡���� �޼ҵ�
	public String getReleaseDate() { // �뷡 �� � ���� �� �Ľ��� �̷�����ٸ� �� ���� �߸����� ��ȯ�ϴ� �޼ҵ�
		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}
		if (songCount == 1) // �� �Ľ��� �̷�����ٸ�
			return songDetailInfo.get("releaseDate").toString();
		
		System.out.println("getReleaseDate() : " + isOnlyDetailParse);
		return null;
	} // String getReleaseDate()
	
	// songDetailDataParsing �Ŀ��� ��밡���� �޼ҵ�
	public String getReleaseDate(JSONObject jObj) { // �뷡 �� � ���� �� �Ľ��� �̷�����ٸ� JSONObject�� �̿��Ͽ� �� ���� �߸����� ��ȯ�ϴ� �޼ҵ�
		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}
		
		if (jObj == null) {
			System.out.println(plzUseRightJSONObject);
			return null;
		}
		
		if (songCount == 1) { // �� �Ľ��� �̷�����ٸ�
			if (jObj.containsKey("releaseDate")) // release key�� ��ȿ�� �˻�
				return jObj.get("releaseDate").toString();
			else {
				System.out.println(jsonDontHaveKey);
				return null;
			}
		}
		
		System.out.println("getReleaseDate(JSONObject jObj) : " + isOnlyDetailParse);
		return null;
	} // String getReleaseDate(JSONObject jObj)
	
	// songDetailDataParsing �Ŀ��� ��밡���� �޼ҵ�
	public String getGenre() { // �뷡 �� � ���� �� ���� �Ľ��� �̷�����ٸ� �� ���� �帣�� ��ȯ�ϴ� �޼ҵ�
		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}
		if (songCount == 1) // �� �Ľ��� �̷�����ٸ�
			return songDetailInfo.get("genre").toString();
		
		System.out.println("getGenre() : " + isOnlyDetailParse);
		return null;
	} // String getGenre()

	// songDetailDataParsing �Ŀ��� ��밡���� �޼ҵ�
	public String getGenre(JSONObject jObj) { // �뷡 �� � ���� �� ���� �Ľ��� �̷�����ٸ� JSONObject�� �̿��Ͽ� �� ���� �帣�� ��ȯ�ϴ� �޼ҵ�
		if (!isParsed()) { // �Ľ��� �̷������ �ʾҴٸ�
			System.out.println(isNotParsed);
			return null;
		}
		
		if (jObj == null) {
			System.out.println(plzUseRightJSONObject);
			return null;
		}
		
		if (songCount == 1) { // �뷡 �� � ���� �� �Ľ��� �̷�����ٸ�
			if (jObj.containsKey("genre")) // genre key�� ��ȿ�� �˻�
				return jObj.get("genre").toString();
			else {
				System.out.println(jsonDontHaveKey);
				return null;
			}
		}
		
		System.out.println("getGenre(JSONObject jObj) : " + isOnlyDetailParse);
		return null;
	} // String getGenre(JSONObject jObj)
} // MelonChartParser class
