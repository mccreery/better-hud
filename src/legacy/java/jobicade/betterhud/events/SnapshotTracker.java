package jobicade.betterhud.events;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import jobicade.betterhud.render.GlSnapshot;

/**
 * Encapsulates behaviour of logging unforseen OpenGL state changes.
 * The tracker will never print the same message twice in a row.
 */
public class SnapshotTracker {
    private final Logger logger;
    private GlSnapshot lastWarningPre  = null;
    private GlSnapshot lastWarningPost = null;

    /**
     * Constructor for trackers.
     * @param logger The logger to send warnings and info to.
     */
    public SnapshotTracker(Logger logger) {
        this.logger = logger;
    }

    /**
     * Warns if the two snapshots are different, or if the difference
     * between them has changed since the last call. When the difference
     * is gone, an info message is logged. Will never log the same
     * message twice in a row.
     *
     * @param pre A snapshot taken before some sequence of actions
     * @param post A snapshot taken after some sequence of actions
     */
    public void step(GlSnapshot pre, GlSnapshot post) {
        if(!pre.equals(post)) {
            if(!pre.equals(lastWarningPre) || !post.equals(lastWarningPost)) {
                logger.printf(Level.WARN, "OpenGL state inconsistency\nPre:  %s\nPost: %s", pre, post);
                lastWarningPre = pre;
                lastWarningPost = post;
            }
        } else if(lastWarningPre != null || lastWarningPost != null) {
            logger.log(Level.INFO, "OpenGL inconsistency resolved");
            lastWarningPre = null;
            lastWarningPost = null;
        }
    }
}
