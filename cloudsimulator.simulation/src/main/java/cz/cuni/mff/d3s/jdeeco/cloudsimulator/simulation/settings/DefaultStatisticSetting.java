package cz.cuni.mff.d3s.jdeeco.cloudsimulator.simulation.settings;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("Default")
public class DefaultStatisticSetting {

	@XStreamAlias("Save")
	@XStreamAsAttribute
	private final String save;

	public String getSave() {
		return save;
	}

	public DefaultStatisticSetting(String save) {
		this.save = save;
	}
}
