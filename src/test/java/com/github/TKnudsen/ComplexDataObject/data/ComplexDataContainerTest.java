package com.github.TKnudsen.ComplexDataObject.data;

import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import com.github.TKnudsen.ComplexDataObject.data.ComplexDataContainer;
import com.github.TKnudsen.ComplexDataObject.data.ComplexDataObject;
import com.github.TKnudsen.ComplexDataObject.tools.ComplexDataObjectFactory;

public class ComplexDataContainerTest {

	@Test
	public void shouldConstructWithObjects() {
		final List<ComplexDataObject> objects = Arrays.asList(
				ComplexDataObjectFactory.createObject("Att A", new Double(2.0), "Att B", "asdf"),
				ComplexDataObjectFactory.createObject("Att A", new Double(3.0), "Att B", "jkl�")
		);

		final ComplexDataContainer complexDataContainer = new ComplexDataContainer(objects);

		assertThat(complexDataContainer).hasSameElementsAs(objects);
	}
}
