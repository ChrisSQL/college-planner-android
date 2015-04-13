package com.chris.collegeplanner.objects;

/**
 * Created by Chris on 10/03/2015.
 */

public class GroupNote {

    private String groupNoteId;
    private String groupNoteAuthor;
    private String groupNoteDatePosted;
    private String groupNoteText;
    private String groupNoteSubject;

    public GroupNote(String groupNoteId, String groupNoteAuthor, String groupNoteDatePosted, String groupNoteText, String groupNoteSubject) {
        this.groupNoteId = groupNoteId;
        this.groupNoteAuthor = groupNoteAuthor;
        this.groupNoteDatePosted = groupNoteDatePosted;
        this.groupNoteText = groupNoteText;
        this.groupNoteSubject = groupNoteSubject;
    }

    public GroupNote() {

    }

    public String getGroupNoteSubject() {
        return groupNoteSubject;
    }

    public void setGroupNoteSubject(String groupNoteSubject) {
        this.groupNoteSubject = groupNoteSubject;
    }

    public String getGroupNoteAuthor() {
        return groupNoteAuthor;
    }

    public void setGroupNoteAuthor(String groupNoteAuthor) {
        this.groupNoteAuthor = groupNoteAuthor;
    }

    public String getGroupNoteDatePosted() {
        return groupNoteDatePosted;
    }

    public void setGroupNoteDatePosted(String groupNoteDatePosted) {
        this.groupNoteDatePosted = groupNoteDatePosted;
    }

    public String getGroupNoteText() {
        return groupNoteText;
    }

    public void setGroupNoteText(String groupNoteText) {

        this.groupNoteText = groupNoteText;
    }

    public String getGroupNoteId() {
        return groupNoteId;
    }

    public void setGroupNoteId(String groupNoteId) {
        this.groupNoteId = groupNoteId;
    }
}
