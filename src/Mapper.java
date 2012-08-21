
/*
(c) Copyright 2011, Francisco Caraballo La Riva.
This program is free software: you can redistribute it and/or modify
it in any way.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
*/

import sim.util.*;
public class Mapper {

	public boolean[][] bitmap;
	public int scale;
	public Mapper (boolean[][] bitmap, int size){
		this.bitmap = bitmap;
//		int l =bitmap.length;
		scale = bitmap.length / size; 
	}
	
	public boolean isInMap(int x, int y){
		return bitmap[x/scale][y/scale];
	}
	
	public Int2D closestPoint(Int2D loc){
		Int2D obj =null;
		int x = loc.x/scale;
		int y = loc.y/scale;
//		int n = 1;
		for (int i=1;i<bitmap.length;i++){
			try {
				if (bitmap[x+i][y]){
					return new Int2D((x+i)*scale,y*scale);
				}
			} catch (Exception e) {
			}
			try {
				if (bitmap[x-i][y]){
					return new Int2D((x-i)*scale,y*scale);
				}
			} catch (Exception e) {
			}			
			try {
				if (bitmap[x][y+i]){
					return new Int2D(x*scale,(y+i)*scale);
				}
			} catch (Exception e) {
			}
			try {
				if (bitmap[x][y-i]){
					return new Int2D(x*scale,(y-i)*scale);
				}
			} catch (Exception e) {
			}
			try {
				if (bitmap[x+i][y+i]){
					return new Int2D((x+i)*scale,(y+i)*scale);
				}
			} catch (Exception e) {
			}
			try {
				if (bitmap[x-i][y-i]){
					return new Int2D((x-i)*scale,(y-i)*scale);
				}
			} catch (Exception e) {
			}
			try {
				if (bitmap[x+i][y-i]){
					return new Int2D((x+i)*scale,(y-i)*scale);
				}
			} catch (Exception e) {
			}
			try {
				if (bitmap[x-i][y+i]){
					return new Int2D((x-i)*scale,(y+i)*scale);
				}
			} catch (Exception e) {
			}
		}
		return obj;
	}
	
}
