package domain;

public class Query {
	
	private String name;	
	private String queryString;

	public Query(String name, String description, String queryString) {
		this.name = name;
		this.queryString = queryString;
	}

	public String getName() {
		return name;
	}

	public String getQueryString() {
		return queryString;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
