package com.sidooo.saic;

public class SaicImage {
	
	private Long id;
	private String image;

	public SaicImage(Long id, String image) {
		this.id = id;
		this.image = image;
	}
	
	public long getId() {
		return this.id.longValue();
	}
	
	public String getImage() {
		return this.image;
	}
}
