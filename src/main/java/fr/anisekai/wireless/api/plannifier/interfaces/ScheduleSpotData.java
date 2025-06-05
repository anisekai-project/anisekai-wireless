package fr.anisekai.wireless.api.plannifier.interfaces;

import fr.anisekai.wireless.api.plannifier.interfaces.entities.Planifiable;
import fr.anisekai.wireless.api.plannifier.interfaces.entities.WatchTarget;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * Represents metadata for a scheduled watch session, including timing, episode count, and skip preferences.
 * <p>
 * This interface abstracts the core information required to define a watchable "spot" on a schedule — such as the content being
 * watched, when it starts, how long it lasts, and whether opening/ending skips are enabled.
 * </p>
 *
 * @param <T>
 *         The type of {@link WatchTarget} being scheduled.
 */
public interface ScheduleSpotData<T extends WatchTarget> {

    /**
     * Retrieve the {@link WatchTarget} that will be watched during the {@link ScheduleSpotData}.
     *
     * @return The watch target
     */
    @NotNull T getWatchTarget();

    /**
     * Define the {@link WatchTarget} that will be watched during the {@link ScheduleSpotData}.
     *
     * @param watchTarget
     *         The watch target
     */
    void setWatchTarget(@NotNull T watchTarget);

    /**
     * Retrieve the {@link ZonedDateTime} at which this {@link ScheduleSpotData} will take place.
     *
     * @return A {@link ZonedDateTime}
     */
    @NotNull ZonedDateTime getStartingAt();

    /**
     * Define the {@link ZonedDateTime} at which this {@link ScheduleSpotData} will take place.
     *
     * @param time
     *         A {@link ZonedDateTime}
     */
    void setStartingAt(@NotNull ZonedDateTime time);

    /**
     * Retrieve the amount of episode that will be watched during this {@link ScheduleSpotData}. If not applicable, just return
     * 1.
     *
     * @return The amount of episode that will be watched.
     */
    long getEpisodeCount();

    /**
     * Define the amount of episode that will be watched during this {@link ScheduleSpotData}. If not applicable, just set 1.
     *
     * @param episodeCount
     *         The amount of episode that will be watched.
     */
    void setEpisodeCount(long episodeCount);

    /**
     * Check if skips for opening and ending are enabled. Check {@link #setSkipEnabled(boolean)} for more details.
     *
     * @return True if superfluous opening and ending should be skipped, false otherwise.
     */
    boolean isSkipEnabled();

    /**
     * Enable or disable skipping of openings and endings, typically for anime content. If enabled and more than one episode is
     * scheduled, the total duration will be reduced by 3 minutes per additional episode to account for skipped openings and
     * endings.
     * <p>
     * <b>Details:</b> Anime episodes typically have a 1m30 opening and a 1m30 ending. When multiple episodes are scheduled
     * and skipping is enabled, only the opening of the first and the ending of the last episode will be played. Each intermediate
     * episode will therefore skip both its opening and ending — reducing the total duration by 3 minutes per skipped episode.
     * </p>
     *
     * @param skipEnabled
     *         True if intermediate openings and endings should be skipped, false otherwise.
     */
    void setSkipEnabled(boolean skipEnabled);

    /**
     * Delay the starting date of this {@link Planifiable} by the provided {@link Duration}.
     *
     * @param modBy
     *         The {@link Duration} to delay
     */
    default void delayBy(@NotNull Duration modBy) {

        this.setStartingAt(this.getStartingAt().plus(modBy));
    }

    /**
     * Retrieve the {@link ZonedDateTime} at which this {@link Planifiable} will end, computed as
     * {@code getStartingAt() + getDuration()}
     *
     * @return A {@link ZonedDateTime}
     */
    default @NotNull ZonedDateTime getEndingAt() {

        return this.getStartingAt().plus(this.getDuration());
    }

    /**
     * Calculates the total {@link Duration} of this {@link Planifiable}, based on the episode count and episode duration. If
     * {@link #isSkipEnabled()} is true and more than one episode is scheduled, the duration is reduced by 3 minutes for each
     * skipped episode.
     *
     * @return The total duration.
     *
     * @see #setSkipEnabled(boolean)
     */
    default @NotNull Duration getDuration() {

        if (this.getEpisodeCount() == 1) return Duration.ofMinutes(this.getWatchTarget().getEpisodeDuration());

        long totalRuntime       = this.getWatchTarget().getEpisodeDuration() * this.getEpisodeCount();
        long superfluousRuntime = this.isSkipEnabled() ? (this.getEpisodeCount() - 1) * 3 : 0;

        return Duration.ofMinutes(totalRuntime - superfluousRuntime);
    }

}
