package beam.integration.ridehail

import beam.integration.TestConstants
import beam.sim.config.MatSimBeamConfigBuilder
import beam.utils.TestConfigUtils.testConfig
import com.typesafe.config.{Config, ConfigValueFactory}
import org.matsim.core.config.{Config => MatSimConfig}

object RideHailTestHelper {

  def buildConfig(allocationManagerName: String): Config = {
    val config = testConfig("test/input/beamville/beam.conf")
      .withValue("beam.outputs.events.fileOutputFormats", ConfigValueFactory.fromAnyRef("xml,csv"))
      .withValue(
        "beam.agentsim.agents.rideHail.allocationManager.name",
        ConfigValueFactory.fromAnyRef(allocationManagerName)
      )
      .withValue(
        TestConstants.KEY_AGENT_MODAL_BEHAVIORS_MODE_CHOICE_CLASS,
        ConfigValueFactory.fromAnyRef("ModeChoiceRideHailIfAvailable")
      )
      .withValue(
        "beam.agentsim.agents.rideHail.numDriversAsFractionOfPopulation",
        ConfigValueFactory.fromAnyRef(0.1)
      )
      .withValue("beam.debug.skipOverBadActors", ConfigValueFactory.fromAnyRef(true))
      .resolve()

    config
  }

  def buildMatsimConfig(config: Config): MatSimConfig = {
    val configBuilder = new MatSimBeamConfigBuilder(config)

    val matsimConfig = configBuilder.buildMatSamConf()
    matsimConfig.controler().setLastIteration(0)
    matsimConfig.planCalcScore().setMemorizingExperiencedPlans(true)
    matsimConfig
  }

}
