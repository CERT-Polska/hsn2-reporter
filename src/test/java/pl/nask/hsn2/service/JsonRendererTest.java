package pl.nask.hsn2.service;

import junit.framework.Assert;
import mockit.Mocked;

import org.testng.annotations.Test;

import pl.nask.hsn2.ResourceException;
import pl.nask.hsn2.connector.REST.DataStoreConnector;
import pl.nask.hsn2.protobuff.Object.Attribute.Type;
import pl.nask.hsn2.protobuff.Object.ObjectData;
import pl.nask.hsn2.wrappers.ObjectDataReportingWrapper;
import pl.nask.hsn2.wrappers.ObjectDataWrapper;

public class JsonRendererTest {
	private static final String EXPECTED_JSON = "{\"id\":1,\"age\":11,\"name\":\"Name Surname\",\"msg\":\"This is message.\"}";
	private static final String JSON_TEMPLATE_PATH = "src/test/resources";
	private static final String JSON_TEMPLATE_FILE = "json.template";

	@Mocked
	DataStoreConnector dsConnector;

	@Test
	public void jsonRendererTest() throws ResourceException {
		TemplateRegistry templateRegistry = new CachingTemplateRegistry(false, JSON_TEMPLATE_PATH);
		JsonRendererImpl renderer = new JsonRendererImpl(templateRegistry);
		pl.nask.hsn2.protobuff.Object.Attribute aId = pl.nask.hsn2.protobuff.Object.Attribute.newBuilder().setName("id").setType(Type.INT)
				.setDataInt(1).build();
		pl.nask.hsn2.protobuff.Object.Attribute aAge = pl.nask.hsn2.protobuff.Object.Attribute.newBuilder().setName("age")
				.setType(Type.INT).setDataInt(11).build();
		pl.nask.hsn2.protobuff.Object.Attribute aName = pl.nask.hsn2.protobuff.Object.Attribute.newBuilder().setName("name")
				.setType(Type.STRING).setDataString("Name Surname").build();
		pl.nask.hsn2.protobuff.Object.Attribute aMsg = pl.nask.hsn2.protobuff.Object.Attribute.newBuilder().setName("msg")
				.setType(Type.STRING).setDataString("This is message.").build();
		ObjectData objectData = ObjectData.newBuilder().setId(1L).addAttrs(aId).addAttrs(aName).addAttrs(aMsg).addAttrs(aAge).build();
		ObjectDataWrapper data = new ObjectDataWrapper(objectData);
		ObjectDataReportingWrapper dataWrapper = new ObjectDataReportingWrapper(1L, data, dsConnector);
		JsonRenderingResult result = renderer.render(dataWrapper, JSON_TEMPLATE_FILE);

		Assert.assertEquals(EXPECTED_JSON, result.getDocument());
	}
}
