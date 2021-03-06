/*-
 * #%L
 * MATSim Episim
 * %%
 * Copyright (C) 2020 matsim-org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.matsim.run.modules;

import com.google.inject.Provides;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.episim.EpisimConfigGroup;
import org.matsim.episim.EpisimPerson;
import org.matsim.episim.EpisimUtils;
import org.matsim.episim.EpisimUtils.Extrapolation;
import org.matsim.episim.TracingConfigGroup;
import org.matsim.episim.model.FaceMask;
import org.matsim.episim.model.Transition;
import org.matsim.episim.policy.FixedPolicy;
import org.matsim.episim.policy.FixedPolicy.ConfigBuilder;
import org.matsim.episim.policy.Restriction;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.matsim.episim.model.Transition.to;

/**
 * Snz scenario for Berlin.
 *
 * @see AbstractSnzScenario
 */
public class SnzBerlinWeekScenario2020 extends AbstractSnzScenario2020 {

	/**
	 * Sample size of the scenario (Either 25 or 100)
	 */
	private final int sample;

	public SnzBerlinWeekScenario2020() {
		this(25);
	}

	public SnzBerlinWeekScenario2020(int sample) {
		this.sample = sample;
	}

	@Provides
	@Singleton
	public Config config() {

		Config config = new SnzBerlinScenario25pct2020().config();

		EpisimConfigGroup episimConfig = ConfigUtils.addOrGetModule(config, EpisimConfigGroup.class);

		episimConfig.clearInputEventsFiles();

		config.plans().setInputFile(SnzBerlinScenario25pct2020.INPUT.resolve(String.format(
				"be_2020-week_snz_entirePopulation_emptyPlans_withDistricts_%dpt_split.xml.gz", sample)).toString());

		episimConfig.addInputEventsFile(SnzBerlinScenario25pct2020.INPUT.resolve(String.format(
				"be_2020-week_snz_episim_events_wt_%dpt_split.xml.gz", sample)).toString())
				.addDays(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);

		episimConfig.addInputEventsFile(SnzBerlinScenario25pct2020.INPUT.resolve(String.format(
				"be_2020-week_snz_episim_events_sa_%dpt_split.xml.gz", sample)).toString())
				.addDays(DayOfWeek.SATURDAY);

		episimConfig.addInputEventsFile(SnzBerlinScenario25pct2020.INPUT.resolve(
				String.format("be_2020-week_snz_episim_events_so_%dpt_split.xml.gz", sample)).toString())
				.addDays(DayOfWeek.SUNDAY);

		if (sample == 100) {
			episimConfig.setInitialInfections(4);
			episimConfig.setSampleSize(1);
		}

		episimConfig.setCalibrationParameter(1.18e-5);
		episimConfig.setStartDate("2020-02-18");

		//episimConfig.setWriteEvents(EpisimConfigGroup.WriteEvents.tracing);

		config.controler().setOutputDirectory(config.controler().getOutputDirectory() + "-week-" + episimConfig.getCalibrationParameter());

		return config;
	}

}
