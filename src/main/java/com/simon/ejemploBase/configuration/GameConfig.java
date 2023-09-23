package com.simon.ejemploBase.configuration;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "gameConfig")
public class GameConfig implements Serializable {
	private int colorsToShow;
	private int minDisplayTime;
	private int maxDisplayTime;
	private int maxUserResponseTime;

	public GameConfig() {
		this(4, 1000, 3000, 3000);
	}
	
	public GameConfig(int colorsToShow, int minDisplayTime, int maxDisplayTime, int maxUserResponseTime) {
		this.colorsToShow = colorsToShow;	// Número de colores a mostrar (wedges) pueden ser 4, 5 o 6
		this.minDisplayTime = minDisplayTime;	// Tiempo mínimo de visualización de un color
		this.maxDisplayTime = maxDisplayTime;	// Tiempo máximo de visualización de un color
		this.maxUserResponseTime = maxUserResponseTime;	// Tiempo máximo de respuesta del usuario
	}

	public int getColorsToShow() {
		return colorsToShow;
	}

	public void setColorsToShow(int colorsToShow) {
		this.colorsToShow = colorsToShow;
	}

	public int getMinDisplayTime() {
		return minDisplayTime;
	}

	public void setMinDisplayTime(int minDisplayTime) {
		this.minDisplayTime = minDisplayTime;
	}

	public int getMaxDisplayTime() {
		return maxDisplayTime;
	}

	public void setMaxDisplayTime(int maxDisplayTime) {
		this.maxDisplayTime = maxDisplayTime;
	}

	public int getMaxUserResponseTime() {
		return maxUserResponseTime;
	}

	public void setMaxUserResponseTime(int maxUserResponseTime) {
		this.maxUserResponseTime = maxUserResponseTime;
	}
}
