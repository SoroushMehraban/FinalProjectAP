package com.Panels.CenterPanelSections;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class SharedSongPanel extends MusicPanel {
    private String title;
    private String artist;
    private ArrayList<SharedSongPanel> sharedSongPanels;

    public SharedSongPanel(BufferedImage defaultImage, String songTitle, String songArtist, ArrayList<SharedSongPanel> sharedSongPanels) {
        super(defaultImage, songTitle, songArtist);
        this.title = songTitle;
        this.artist = songArtist;
        this.sharedSongPanels = sharedSongPanels;
        createRadioSongListener();
    }

    public String getTitle() {
        return title;
    }

    /**
     * this method creates a Mouse listener for radio song panel
     * it does two things:
     * -when mouse entered: it changes to a brighter color.
     * -when mouse exited: it backs to previous color it had.
     * -when mouse clicked: it try to download if it's clicked first, and plays it.
     */
    private void createRadioSongListener() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SharedSongPanel source = (SharedSongPanel) e.getSource();//source is a song panel which mouse clicked on it.
                //if clicked in order to play song:
                //play clicked music:
                for (int i = 0; i < sharedSongPanels.size() - 1 ; i++)
                    if(sharedSongPanels.get(i).getTitle().equals(source.getTitle())) {
                        System.out.println("index clicked: " + i);
                        break;
                    }
                //GUIFrame.playClickedMusic(source);//playing music
            }

            @Override
            public void mouseExited(MouseEvent e) {
                MusicPanel source = (MusicPanel) e.getSource();
                source.setBackground(new Color(23, 23, 23));
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                MusicPanel source = (MusicPanel) e.getSource();
                source.setBackground(new Color(41, 41, 41));
            }
        });
    }
}