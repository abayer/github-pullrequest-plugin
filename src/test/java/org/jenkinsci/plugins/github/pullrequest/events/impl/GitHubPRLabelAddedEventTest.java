package org.jenkinsci.plugins.github.pullrequest.events.impl;

import hudson.model.TaskListener;
import org.jenkinsci.plugins.github.pullrequest.GitHubPRCause;
import org.jenkinsci.plugins.github.pullrequest.GitHubPRLabel;
import org.jenkinsci.plugins.github.pullrequest.GitHubPRPullRequest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.github.*;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

/**
 * @author Alina Karpovich 
 */
@RunWith(MockitoJUnitRunner.class)
public class GitHubPRLabelAddedEventTest {
    
    private static final String MERGE = "merge";
    private static final String REVIEWED = "reviewed";
    private static final String LOCALLY_TESTED = "locally tested";

    @Mock private GHPullRequest remotePr;
    @Mock private GitHubPRPullRequest localPR;
    @Mock private GitHubPRLabel labels;
    @Mock private GHRepository repository;
    @Mock private GHIssue issue;
    @Mock private GHLabel mergeLabel;
    @Mock private GHLabel reviewedLabel;
    @Mock private GHLabel testLabel;
    @Mock private TaskListener listener;
    @Mock private PrintStream logger;

    private Set<String> checkedLabels = new HashSet<String>();

    {
        checkedLabels.add(MERGE);
        checkedLabels.add(REVIEWED);
        checkedLabels.add(LOCALLY_TESTED);
    }

    /**
     * Case when there is three checked labels and there is one that was added and one that already exists.
     */
    @Test
    public void secondOfThreeLabelsWasAdded() throws IOException {
        Set<String> localLabels = new HashSet<>();
        localLabels.add(LOCALLY_TESTED);
        
        List<GHLabel> remoteLabels = new ArrayList<GHLabel>();
        remoteLabels.add(testLabel);
        remoteLabels.add(reviewedLabel);

        commonExpectations(localLabels);
        when(issue.getLabels()).thenReturn(remoteLabels);
        when(testLabel.getName()).thenReturn(LOCALLY_TESTED);
        when(reviewedLabel.getName()).thenReturn(REVIEWED);
        
        GitHubPRLabelAddedEvent instance = new GitHubPRLabelAddedEvent(labels);
        GitHubPRCause cause = instance.check(null, remotePr, localPR, listener);
        Assert.assertNull(cause);
    }

    /**
     * Case when there is three checked labels and all of them was already added.
     */
    @Test
    @Ignore
    public void thirdOfThreeLabelsWasAdded() throws IOException {
        Set<String> localLabels = new HashSet<>();
        localLabels.add(LOCALLY_TESTED);
        localLabels.add(MERGE);

        List<GHLabel> remoteLabels = new ArrayList<GHLabel>();
        remoteLabels.add(testLabel);
        remoteLabels.add(reviewedLabel);
        remoteLabels.add(mergeLabel);

        commonExpectations(localLabels);
        when(issue.getLabels())
                .thenReturn(remoteLabels);
        when(testLabel.getName())
                .thenReturn(LOCALLY_TESTED);
        when(reviewedLabel.getName())
                .thenReturn(REVIEWED);
        when(mergeLabel.getName())
                .thenReturn(MERGE);
        causeCreationExpectations();

        GitHubPRLabelAddedEvent instance = new GitHubPRLabelAddedEvent(labels);
        GitHubPRCause cause = instance.check(null, remotePr, localPR, listener);
        Assert.assertEquals(localLabels, cause.getLabels());
    }

    /**
     * Case when there is three checked labels and all of them was already added.
     */
    @Test
    public void allLabelsAlreadyExist() throws IOException {
        Set<String> localLabels = new HashSet<>();
        localLabels.add(LOCALLY_TESTED);
        localLabels.add(MERGE);
        localLabels.add(REVIEWED);

        List<GHLabel> remoteLabels = new ArrayList<GHLabel>();
        remoteLabels.add(testLabel);
        remoteLabels.add(reviewedLabel);
        remoteLabels.add(mergeLabel);

        commonExpectations(localLabels);
        when(issue.getLabels())
                .thenReturn(remoteLabels);
        when(testLabel.getName())
                .thenReturn(LOCALLY_TESTED);
        when(reviewedLabel.getName())
                .thenReturn(REVIEWED);
        when(mergeLabel.getName())
                .thenReturn(MERGE);

        GitHubPRLabelAddedEvent instance = new GitHubPRLabelAddedEvent(labels);
        GitHubPRCause cause = instance.check(null, remotePr, localPR, listener);
        Assert.assertNull(cause);
    }

    /**
     * Case when there is three checked labels and no one of them was removed.
     */
    @Test
    public void noLabelsWasRemoved() throws IOException {
        Set<String> localLabels = new HashSet<>();
        localLabels.add(MERGE);
        localLabels.add(REVIEWED);
        localLabels.add(LOCALLY_TESTED);

        List<GHLabel> remoteLabels = new ArrayList<GHLabel>();
        remoteLabels.add(testLabel);
        remoteLabels.add(reviewedLabel);
        remoteLabels.add(mergeLabel);

        commonExpectations(localLabels);
        when(issue.getLabels())
                .thenReturn(remoteLabels);
        when(testLabel.getName())
                .thenReturn(LOCALLY_TESTED);
        when(reviewedLabel.getName())
                .thenReturn(REVIEWED);
        when(mergeLabel.getName())
                .thenReturn(MERGE);

        GitHubPRLabelAddedEvent instance = new GitHubPRLabelAddedEvent(labels);
        GitHubPRCause cause = instance.check(null, remotePr, localPR, listener);
        Assert.assertNull(cause);
    }
    
    private void commonExpectations(Set<String> localLabels) throws IOException {
        when(labels.getLabelsSet()).thenReturn(checkedLabels);
        when(localPR.getLabels()).thenReturn(localLabels);
        when(remotePr.getState()).thenReturn(GHIssueState.OPEN);
        when(remotePr.getRepository()).thenReturn(repository);
        when(repository.getIssue(anyInt())).thenReturn(issue);
        when(repository.getOwnerName()).thenReturn("ownerName");
        when(listener.getLogger()).thenReturn(logger);
    }
    
    private void causeCreationExpectations() throws IOException {
        GHUser mockUser = mock(GHUser.class);
        GHCommitPointer mockPointer = mock(GHCommitPointer.class);
        
        when(remotePr.getUser()).thenReturn(mockUser);
        when(remotePr.getHead()).thenReturn(mockPointer);
        when(remotePr.getBase()).thenReturn(mockPointer);
    }
}
