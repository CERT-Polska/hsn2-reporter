package jsontemplate;

import static jsontemplate.TemplateUtils.createTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import pl.nask.hsn2.jsontemplate.formatters.JsonAttachment;

public class SectionWithBooleanTest {
	
	@DataProvider(name="dataProvider")
	public Object[][] dataProvider() {
		return new Object[][] {
				{null, "empty"},
				{true, "field:true"},
				{false, "empty"}
		};
	}
	
	@Test(dataProvider="dataProvider")
	public void shouldBeRendered(Boolean fieldValue, String expectedResult) {
		String strTemplate = 
				"<<.section field>>"
						+ "field:<<@>>"
						+ "<<.or>>"
						+ "empty"
						+ "<<.end>>";
		Template template = createTemplate(strTemplate, new ArrayList<JsonAttachment>());

		Map<String, Object> context = new HashMap<>();
		context.put("field", fieldValue);

		String result = template.expand(context);
		Assert.assertEquals(expectedResult, result);		
	}

}
