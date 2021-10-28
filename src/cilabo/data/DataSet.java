package cilabo.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataSet {
	// ************************************************************
	// Fields

	int DataSize;	// Number of Patterns
	int Ndim;	// Number of Features
	int Cnum;	// Number of Classes

	ArrayList<Pattern> patterns = new ArrayList<>();

	// ************************************************************
	// Constructor

	public DataSet() {}

	// ************************************************************
	// Methods

	/* Shallow Copy */
	public void addPattern(Pattern pattern) {
		this.patterns.add(pattern);
	}

	public Pattern getPattern(int index) {
		return this.patterns.get(index);
	}

	public Pattern getPatternWithID(int id) {
		List<Pattern> list = this.patterns.stream()
										.filter(p -> p.getID() == id)
										.collect( Collectors.toList() );
		return list.get(0);
	}

	public void setPattern(ArrayList<Pattern> patterns) {
		this.patterns = patterns;
	}

	public ArrayList<Pattern> getPatterns(){
		return this.patterns;
	}

	@Override
	public String toString() {
		if(this.patterns.size() == 0) {
			return null;
		}
		String ln = System.lineSeparator();
		String str = "";
		// Header
		str += this.DataSize + "," + this.Ndim + "," + this.Cnum + ln;
		// Patterns
		for(int n = 0; n < this.patterns.size(); n++) {
			Pattern pattern = this.patterns.get(n);
			str += pattern.toString() + ln;
		}
		return str;
	}

	public void setNdim(int Ndim) {
		this.Ndim = Ndim;
	}

	public int getNdim() {
		return this.Ndim;
	}

	public void setCnum(int Cnum) {
		this.Cnum = Cnum;
	}

	public int getCnum() {
		return this.Cnum;
	}

	public void setDataSize(int DataSize) {
		this.DataSize = DataSize;
	}

	public int getDataSize() {
		return this.DataSize;
	}



	//並列分散実装用 (ver. 21以下)
	// ************************************************************
	// Fields

//	int setting = 0;
//	InetSocketAddress[] serverList = null;

	// ************************************************************
	// Constructor

	//並列分散実装用 (ver. 21以下)
//	public DataSetInfo(int Datasize, int Ndim, int Cnum, int setting, InetSocketAddress[] serverList){
//		this.DataSize = Datasize;
//		this.Ndim = Ndim;
//
//		this.setting = setting;
//		this.serverList = serverList;
//	}
//
//	public DataSetInfo(int Ndim, int Cnum, int DataSize, ArrayList<Pattern> patterns) {
//		this.Ndim = Ndim;
//		this.DataSize = DataSize;
//		this.patterns = patterns;
//	}

	// ************************************************************
	// Methods

//	public int getSetting() {
//		return this.setting;
//	}
//
//	public void setSetting(int setting) {
//		this.setting = setting;
//	}
//
//	public InetSocketAddress[] getServerList() {
//		return this.serverList;
//	}
//
//	public void setServerList(InetSocketAddress[] serverList) {
//		this.serverList = serverList;
//	}

}
