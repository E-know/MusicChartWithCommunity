import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MelonChartParser implements MusicChartParser{

	private int songCount;
	private String url;
	private JSONArray chartList;
	private Thread parsingThread;
	
	private String isNotParsed = "�Ľ��� ���������� �̷������ �ʾҽ��ϴ� :(";
	
	public MelonChartParser() {
		songCount = 0;
		url = "https://www.melon.com/chart/index.htm";
		chartList = null;
		//parsingThread = new Thread();
	}
	
	@Override
	public void htmlDataParsing() {
		// ��� ��Ʈ 1~100���� �뷡�� �Ľ���
		songCount = 0;
		chartList = null;
		
		try {
			// ��� ��Ʈ ���ῡ �ʿ��� header ���� �� ����
			Connection melonConnection = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36")
					.header("Upgrade-Insecure-Requests", "1")
					.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
					.method(Connection.Method.GET);
			
			// ���� �� ���������� �ܾ��
			Document melonDocument = melonConnection.get();
			
			// 1~50���� ���� ������ �ҷ���, ������ ���� ���� ������ �̱� ���� ��ũ�� �̴� �뵵�� ���
			Elements data1st50 = melonDocument.select("tr.lst50");
		
			chartList = new JSONArray();
			
			for (Element elem : data1st50) {
				// JSONObject�� �����͸� �ֱ� ���� �۾�
				HashMap<String, Object> songAllInfo = new HashMap<String, Object>();
				
				// key : rank, value : ����
				songAllInfo.put("rank", elem.select("span.rank").first().text());
				
				// songId�� ���� � ���� ���� ������ ��� ���� ����
				Connection songDetailConnection = Jsoup.connect("https://www.melon.com/song/detail.htm?songId=" + elem.attr("data-song-no"))
						.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36")
						.header("Upgrade-Insecure-Requests", "1")
						.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
						.method(Connection.Method.GET);
				
				// � ���� ���� ���� �� �������� �ܾ��
				Document songDetailDocument = songDetailConnection.get();
				Element songDetailInfo = songDetailDocument.select(".wrap_info").first();
				
				String songImageUrl = songDetailInfo.getElementsByTag("img").first().attr("src");
				songAllInfo.put("imageUrl", songImageUrl);
				
				String songName = songDetailInfo.select("div.song_name").first().text();
				songAllInfo.put("title", songName);
				
				Element songDetailEtcInfo = songDetailInfo.select("dl.list").first();
				
				String songAlbumName = songDetailEtcInfo.getElementsByTag("dd").get(0).getElementsByTag("a").first().text();
				songAllInfo.put("albumName", songAlbumName);
				
				String songReleaseDate = songDetailEtcInfo.getElementsByTag("dd").get(1).text();
				songAllInfo.put("releaseDate", songReleaseDate);
				
				String songGenre = songDetailEtcInfo.getElementsByTag("dd").get(2).text();
				songAllInfo.put("genre", songGenre);
				
				JSONObject jsonSongInfo = new JSONObject(songAllInfo);
				
				chartList.add(jsonSongInfo);
				songCount++;
			}
			
			//Elements dataRank = data1st50.select("span.rank");
			//data1st50.remove(0); // �� �� ���Ҵ� �����̹Ƿ� ����
			
			//chartList.add();
			
			//for (Element e : data1st50) {
			//	String test = e.text();
			//	System.out.println(test);
			//}
			//for(Object o: chartList){
			//    if ( o instanceof JSONObject ) {
			//        System.out.println(((JSONObject)o));
			//    }
			//}
			
			//System.out.println("[[ " + ((JSONObject)chartList.get(0)).get("imageUrl").toString().replaceAll("\\\\", "") + " ]]");
			
		}
		catch (Exception e) {
			e.printStackTrace();
			chartList = null;
		}
		
	}

	@Override
	public boolean isParsed() {
		if (chartList == null)
			return false;
		else
			return true;
	}

	@Override
	public JSONArray getChartList() {
		if (!isParsed())
			System.out.println(isNotParsed);
		return chartList;
	}

	@Override
	public JSONObject getSongData(int rank) {
		//
		if (rank < 1 || rank > songCount) {
			System.out.println("1 ~ " + songCount + "�� �̳��� ������ �Է����ּ��� :)");
			return null;
		}
		if (isParsed())
			return (JSONObject)chartList.get(rank - 1);
		else {
			System.out.println(isNotParsed);
			return null;
		}
	}

	@Override
	public JSONObject getSongData(String title) {
		
		if (!isParsed()) {
			System.out.println(isNotParsed);
			return null;
		}
		
		for (int i = 0; i < songCount; i++) {
			if (((JSONObject)chartList.get(i)).get("title") == title)
				return (JSONObject)chartList.get(i);
		}
		// �ݺ��� ������ ��ã���� ������ ���� �� - null ��ȯ
		return null;
	}

	@Override
	public int getRank(String title) {
		
		if (!isParsed()) {
			System.out.println(isNotParsed);
			return -1;
		}
		
		for (int i = 0; i < songCount; i++) {
			if (((JSONObject)chartList.get(i)).get("title") == title)
				return Integer.parseInt(((JSONObject)chartList.get(i)).get("rank").toString());
		}
		// �ݺ��� ������ ��ã���� ������ ���� �� - -1 ��ȯ
		return -1;
	}

	@Override
	public String getTitle(int rank) {
		
		if (rank < 1 || rank > songCount) {
			System.out.println("1~" + songCount + "�� �̳��� ������ �Է����ּ���");
			return null;
		}
		
		if (!isParsed()) {
			System.out.println(isNotParsed);
			return null;
		}
		
		return ((JSONObject)chartList.get(rank - 1)).get("title").toString();
	}

	@Override
	public String getArtistName(int rank) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getArtistName(String title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getArtistName(JSONObject jObj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAlbumName(int rank) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAlbumName(String title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAlbumName(JSONObject jObj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLikeNum(int rank) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLikeNum(String title) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLikeNum(JSONObject jObj) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getImageUrl(int rank) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getImageUrl(String title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getImageUrl(JSONObject jObj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReleaseDate(int rank) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReleaseDate(String title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReleaseDate(JSONObject jObj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getGenre(int rank) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getGenre(String title) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getGenre(JSONObject jObj) {
		// TODO Auto-generated method stub
		return null;
	}

}
