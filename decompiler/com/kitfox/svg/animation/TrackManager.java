package com.kitfox.svg.animation;

import com.kitfox.svg.SVGElementException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;














































public class TrackManager
  implements Serializable
{
  public static final long serialVersionUID = 0L;
  
  static class TrackKey
  {
    String name;
    int type;
    
    TrackKey(AnimationElement base) {
      this(base.getAttribName(), base.getAttribType());
    }

    
    TrackKey(String name, int type) {
      this.name = name;
      this.type = type;
    }


    
    public int hashCode() {
      int hash = (this.name == null) ? 0 : this.name.hashCode();
      hash = hash * 97 + this.type;
      return hash;
    }


    
    public boolean equals(Object obj) {
      if (!(obj instanceof TrackKey)) return false; 
      TrackKey key = (TrackKey)obj;
      return (key.type == this.type && key.name.equals(this.name));
    }
  }
  
  HashMap<TrackKey, TrackBase> tracks = new HashMap<TrackKey, TrackBase>();









  
  public void addTrackElement(AnimationElement element) throws SVGElementException {
    TrackKey key = new TrackKey(element);
    
    TrackBase track = this.tracks.get(key);
    
    if (track == null) {

      
      if (element instanceof Animate) {
        
        switch (((Animate)element).getDataType()) {
          
          case 0:
            track = new TrackDouble(element);
            break;
          case 1:
            track = new TrackColor(element);
            break;
          case 2:
            track = new TrackPath(element);
            break;
          default:
            throw new RuntimeException("");
        } 
      
      } else if (element instanceof AnimateColor) {
        
        track = new TrackColor(element);
      }
      else if (element instanceof AnimateTransform || element instanceof AnimateMotion) {
        
        track = new TrackTransform(element);
      } 
      
      this.tracks.put(key, track);
    } 
    
    track.addElement(element);
  }


  
  public TrackBase getTrack(String name, int type) {
    if (type == 2) {
      
      TrackBase trackBase = getTrack(name, 0);
      if (trackBase != null) return trackBase; 
      trackBase = getTrack(name, 1);
      if (trackBase != null) return trackBase; 
      return null;
    } 

    
    TrackKey key = new TrackKey(name, type);
    TrackBase t = this.tracks.get(key);
    if (t != null) return t;

    
    key = new TrackKey(name, 2);
    return this.tracks.get(key);
  }

  
  public int getNumTracks() {
    return this.tracks.size();
  }

  
  public Iterator<TrackBase> iterator() {
    return this.tracks.values().iterator();
  }
}
