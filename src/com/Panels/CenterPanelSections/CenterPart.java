package com.Panels.CenterPanelSections;

import com.Interfaces.AddingSongLinker;
import com.Interfaces.LikeLinker;
import com.Interfaces.LyricsLinker;
import com.Interfaces.ShowSongsLinker;
import com.MP3.MP3Info;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Center part of CenterPanel.
 * this part shows user albums,playlist, or list of albumSongs based on user choices.
 *
 * @author Soroush Mehraban & Morteza Damghani
 * @version 1.0
 */
public class CenterPart extends JPanel implements ShowSongsLinker, LikeLinker, LyricsLinker, AddingSongLinker {
    private HashMap<String,AlbumPanel> albumPanels;
    private HashMap<String,PlayListPanel> playListPanels;
    private PlayListPanel currentPlaylistPanel;
    private HashSet<SongPanel> currentPlaying;
    private GridBagConstraints constraints;
    private BufferedImage emptyPlayListImage;
    private BufferedImage plusImage;
    private JLabel plusLabel;
    private JLabel addSongToPlayListLabel;
    private boolean AddingSongToPlaylist;

    /**
     * Class Constructor.
     */
    public CenterPart() {
        this.setLayout(new GridBagLayout());//setting panel layout
        constraints = new GridBagConstraints();//creating panel constraints to denote where components should located on.
        constraints.insets = new Insets(0,0,15,15);//denoting spaces between components.
        this.setBackground(new Color(23,23,23));//setting panel background

        albumPanels = new HashMap<>();//list of albumPanels.
        playListPanels = new HashMap<>();

        //creating add song to play list option:
        try {
            plusImage = ImageIO.read(new File("Icons/PlusSong-no-select.png"));
            plusLabel = new JLabel(new ImageIcon(plusImage));
            addSongToPlayListLabel = new JLabel("Add Song to Playlist");
            addSongToPlayListLabel.setForeground(new Color(120,120,120));
            createAddSongToPlayListListener();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading plus song image","An Error Occurred",JOptionPane.ERROR_MESSAGE);
        }
        //creating default playLists:
        createDefaultPlayLists();
        //creating Empty play list picture:
        try {
            emptyPlayListImage = ImageIO.read(new File("Images/EmptyPlayList.jpg"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading empty playlist image","An Error Occurred",JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public boolean isAddingSongToPlaylist() {
        return AddingSongToPlaylist;
    }

    @Override
    public PlayListPanel getCurrentPlaylistPanel() {
        return currentPlaylistPanel;
    }

    public HashSet<SongPanel> getCurrentPlaying() {
        return currentPlaying;
    }

    /**
     * when this method calls, it shows albums in center part of Center panel.
     */
    public void showHome(){
        this.removeAll();//removing all components in center part
        AddingSongToPlaylist = false;
        //initializing grids:
        int gridx = 0;
        int gridy = 0;
        //creating album label to show at top of albums:
        JLabel albumLabel = new JLabel("Albums:");
        albumLabel.setForeground(new Color(219,219,219));
        constraints.gridy = gridy;
        constraints.gridx = gridx;
        this.add(albumLabel, constraints);
        gridy++;//going to next line
        //showing album panels:
        for(AlbumPanel albumPanel: albumPanels.values()){
            constraints.gridx = gridx;
            constraints.gridy = gridy;
            this.add(albumPanel, constraints);
            if(gridx < 3)
                gridx++;
            else{
                gridy++;
                gridx = 0;
            }
        }
        //creating playList label to show at top of playLists:
        gridx--;
        gridy++;
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        JLabel playListLabel = new JLabel("PlayLists:");
        playListLabel.setForeground(new Color(219,219,219));
        this.add(playListLabel, constraints);
        gridy++;
        for(PlayListPanel playListPanel: playListPanels.values()){
            constraints.gridy = gridy;
            constraints.gridx = gridx;
            this.add(playListPanel, constraints);
            if(gridx < 3)
                gridx++;
            else{
                gridy++;
                gridx = 0;
            }
        }
        //updating center part of center panel:
        this.repaint();
        this.revalidate();
    }

    /**
     * this method shows given songPanels in center part of center panel.
     *
     * @param songPanels desired song panel to show.
     */
    @Override
    public void showSongs(HashSet<SongPanel> songPanels){
        this.currentPlaying = songPanels;
        AddingSongToPlaylist = false;
        this.removeAll();//removing all components.
        //initializing grids:
        int gridx = 0;
        int gridy = 0;
        //showing music panels:
        for(SongPanel songPanel: songPanels){
            constraints.gridx = gridx;
            constraints.gridy = gridy;
            this.add(songPanel, constraints);
            if(gridx < 3) {
                gridx++;
            }
            else{
                gridx = 0;
                gridy++;
            }
        }
        //updating center part of center panel:
        this.repaint();
        this.revalidate();
    }

    /**
     * This method only shows song panels related to an album.
     * @param albumTitle title of album as a key
     */
    public void showAlbumSongs(String albumTitle){
        showSongs(albumPanels.get(albumTitle).getSongPanels());
    }


    @Override
    public void showPlayListSongs(String playListTitle){
        currentPlaylistPanel = playListPanels.get(playListTitle);//getting current song where user see.
        showSongs(playListPanels.get(playListTitle).getPlayListSongs());//show all songs related to playlist
        //creating a container to cover add song to playlist option:
        JPanel container = new JPanel();
        container.setOpaque(false);//removing its background
        container.setLayout(new BoxLayout(container,BoxLayout.LINE_AXIS));
        //adding features:
        container.add(plusLabel);
        container.add(Box.createHorizontalStrut(5));//adding spaces between components.
        container.add(addSongToPlayListLabel);
        //adding container at the end:
        constraints.gridy++;
        constraints.gridx = 0;
        this.add(container,constraints);

    }
    /**
     * This method is called when user press Songs in West panel, it shows all songs which exists in library.
     */
    public void showAllSongs(){
        this.removeAll();//removing all components.
        HashSet<SongPanel> allSongs = new HashSet<>();//this helps us to play ordered song in south panel.
        //initializing grids:
        int gridx = 0;
        int gridy = 0;
        //showing all songs:
        for (AlbumPanel albumPanel : albumPanels.values())
            for(SongPanel songPanel : albumPanel.getSongPanels()){
                //this boolean check in case if we show all songs for adding to playlist, it doesn't show song that playlist already has:
                boolean canAdd = !AddingSongToPlaylist || !currentPlaylistPanel.getPlayListSongs().contains(songPanel);
                if(canAdd) {
                    allSongs.add(songPanel);
                    constraints.gridy = gridy;
                    constraints.gridx = gridx;
                    this.add(songPanel, constraints);
                    if (gridx < 3) {
                        gridx++;
                    } else {
                        gridx = 0;
                        gridy++;
                    }
                }
            }
        currentPlaying = allSongs;
        //updating center part of center panel:
        this.repaint();
        this.revalidate();
    }

    /**
     * this method add an album to albumSongs HashMap if it's not exist
     * or add new songs to existing album if given songs are new.
     *
     * @param albumTitle title of album which is a key of HashMap
     * @param albumMusicsInfo list of albumSongs info which has similar albums.
     */
    public void addAlbum(String albumTitle, ArrayList<MP3Info> albumMusicsInfo){
        if(!albumPanels.containsKey(albumTitle)) {//if album is a new album
            AlbumPanel albumPanel = createAlbumPanel(albumMusicsInfo);
            albumPanels.put(albumTitle, albumPanel);
            showHome();//showing home after created new album to show it's added.
        }
        else//if album added before we just add new songs
            albumPanels.get(albumTitle).addNewSongs(albumMusicsInfo,this);

    }

    /**
     *this method create an album panel which if user press it, it shows music panels related to this album.
     * it gives image and title from first mp3info in given ArrayList.
     *
     * @param albumMusicsInfo list of music infos has similar album name
     * @return an album panel
     */
    private AlbumPanel createAlbumPanel(ArrayList<MP3Info> albumMusicsInfo){
        MP3Info firstMP3Info = albumMusicsInfo.get(0);
        AlbumPanel album = null;
        String description = "Album contains "+albumMusicsInfo.size()+" songs";
        try {//creating an album panel with its listener
            album = new AlbumPanel(firstMP3Info.getImage(),firstMP3Info.getTitle(),description,albumMusicsInfo,this,this);
        } catch (InvalidDataException | IOException | UnsupportedTagException e) {
            JOptionPane.showMessageDialog(null, "Error reading mp3 file image","An Error Occurred",JOptionPane.ERROR_MESSAGE);
        }
        return album;
    }

    /**
     * this method creates default play lists which must to exist at first playing of program.(only be called at class constructor).
     */
    private void createDefaultPlayLists(){
        try {
            BufferedImage favoriteSongsImage = ImageIO.read(new File("Images/FavoriteSong.png"));
            PlayListPanel favoriteSongs = new PlayListPanel(favoriteSongsImage,"Favorite Songs","Favorite albumSongs chosen by user",this);
            playListPanels.put("Favorite Songs",favoriteSongs);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading favorite albumSongs image","An Error Occurred",JOptionPane.ERROR_MESSAGE);
        }
        try {
            BufferedImage sharedSongImage = ImageIO.read(new File("Images/SharedSongs.jpg"));
            PlayListPanel sharedSongs = new PlayListPanel(sharedSongImage,"Shared Songs","Shared albumSongs between users",this);
            playListPanels.put("Shared Songs",sharedSongs);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading shared albumSongs image","An Error Occurred",JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void addToFavoritePlayList(String directory) {
        playListPanels.get("Favorite Songs").addSong(directory,this);
    }

    @Override
    public void removeFromFavoritePlayList(String directory) {
        try {
            MP3Info mp3Info = new MP3Info(directory);
            playListPanels.get("Favorite Songs").removeSong(mp3Info.getTitle());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading mp3 file","An Error Occurred",JOptionPane.ERROR_MESSAGE);
        } catch (NoSuchFieldException e) {
            JOptionPane.showMessageDialog(null, "Error finding mp3 file","An Error Occurred",JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public boolean isSongLiked(String directory) {
        PlayListPanel favoriteSongsPlayLists = playListPanels.get("Favorite Songs");
        HashSet<SongPanel> favoriteSongPanels = favoriteSongsPlayLists.getPlayListSongs();
        try {
            MP3Info mp3Info = new MP3Info(directory);
            for(SongPanel songPanel : favoriteSongPanels){
                if(songPanel.getSongTitle().equals(mp3Info.getTitle()))
                 return true;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading mp3 file","An Error Occurred",JOptionPane.ERROR_MESSAGE);
        } catch (NoSuchFieldException e) {
            JOptionPane.showMessageDialog(null, "Error finding mp3 file","An Error Occurred",JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    /**
     * Creating playlist which shows it in center part with an image which said : Empty PlayList
     * @param title title of play list.
     * @param description description to show under the title.
     */
    public void createPlayList(String title, String description){
        if(!playListPanels.containsKey(title)){//if this playlist doesn't exist.
            PlayListPanel newPlayListPanel = new PlayListPanel(emptyPlayListImage,title, description,this);
            playListPanels.put(title,newPlayListPanel);
        }
    }

    /**
     * this method adds a song to given playlist.
     * @param playListTitle  title of playlist as a key of HashMap.
     * @param songDirectory directory of music to add.
     */
    public void addSongToPlayList(String playListTitle, String songDirectory){
        if(playListPanels.containsKey(playListTitle))//if playlist exists
            playListPanels.get(playListTitle).addSong(songDirectory,this);
    }

    /**
     * this method remove a song from given playlist.
     * @param playListTitle title of playlist as a key of HashMap
     * @param songTitle title of song we want to remove.
     */
    public void removeSongFromPlayList(String playListTitle, String songTitle){
        if(playListPanels.containsKey(playListTitle))//if playlist exist
            playListPanels.get(playListTitle).removeSong(songTitle);
    }

    @Override
    public void showLyrics(ArrayList<String> lyricsLines) {
        this.removeAll();//removing all components in center part
        //initializing grids:
        constraints.gridy = 0;
        constraints.gridx = 0;
        for(String lyricsLine : lyricsLines){
            JLabel lineLabel = new JLabel(lyricsLine);
            lineLabel.setForeground(new Color(219,219,219));//setting label color
            this.add(lineLabel,constraints);
            constraints.gridy++;
        }
    }

    /**
     * @return title of play lists, to show as a JLabel in west panel.
     */
    public ArrayList<String> getPlayistTitles(){
        return new ArrayList<>(playListPanels.keySet());
    }

    /**
     * @return title of albums to show as a JLabel in west panel.
     */
    public ArrayList<String> getAlbumTitles(){
        return new ArrayList<>(albumPanels.keySet());
    }
    private void createAddSongToPlayListListener(){
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                AddingSongToPlaylist = true;
                showAllSongs();//show all songs without songs that playlist already has.
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                try {
                    plusImage = ImageIO.read(new File("Icons/PlusSong.png"));
                    plusLabel.setIcon(new ImageIcon(plusImage));
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, "Error reading plus song image","An Error Occurred",JOptionPane.ERROR_MESSAGE);
                }
                addSongToPlayListLabel.setForeground(new Color(179,179,179));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                try {
                    plusImage = ImageIO.read(new File("Icons/PlusSong-no-select.png"));
                    plusLabel.setIcon(new ImageIcon(plusImage));
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, "Error reading plus song image","An Error Occurred",JOptionPane.ERROR_MESSAGE);
                }
                addSongToPlayListLabel.setForeground(new Color(120,120,120));
            }
        };
        plusLabel.addMouseListener(mouseAdapter);
        addSongToPlayListLabel.addMouseListener(mouseAdapter);
    }
}
