package pl.nask.hsn2.jsontemplate.formatters;

import org.testng.Assert;
import org.testng.annotations.Test;

import pl.nask.hsn2.wrappers.HsnContext;

public class JobIdAndObjectIdFormatterTest {
	private static final String TEST_STRING = "test string";

	private static class TestContext implements HsnContext {
		@Override
		public long getObjectId() {
			return 1;
		}

		@Override
		public long getJobId() {
			return 2;
		}
	}

	private static final JobIdFormatter JOB_ID_FORMATTER = new JobIdFormatter();
	private static final ObjectIdFormatter OBJ_ID_FORMATTER = new ObjectIdFormatter();

	@Test
	public void jobIdFormatterWithHsnContext() {
		TestContext testContext = new TestContext();
		Object result = JOB_ID_FORMATTER.format(testContext);
		Assert.assertEquals(result instanceof Long, true);
		Assert.assertEquals((long) result, 2);

		String testString = TEST_STRING;
		result = JOB_ID_FORMATTER.format(testString);
		Assert.assertSame(result, testString);
	}

	@Test
	public void jobIdFormatterWithNonHsnContext() {
		StringBuilder builder = new StringBuilder(TEST_STRING);
		Object result = JOB_ID_FORMATTER.format(builder);

		Assert.assertSame(result, builder);
		Assert.assertEquals(builder.toString(), TEST_STRING);
	}

	@Test
	public void objectIdFormatterWithHsnContext() {
		TestContext testContext = new TestContext();
		Object result = OBJ_ID_FORMATTER.format(testContext);
		Assert.assertEquals(result instanceof Long, true);
		Assert.assertEquals((long) result, 1);

		String testString = TEST_STRING;
		result = JOB_ID_FORMATTER.format(testString);
		Assert.assertSame(result, testString);
	}

	@Test
	public void objectIdFormatterWithNonHsnContext() {
		StringBuilder builder = new StringBuilder(TEST_STRING);
		Object result = OBJ_ID_FORMATTER.format(builder);

		Assert.assertSame(result, builder);
		Assert.assertEquals(builder.toString(), TEST_STRING);
	}
}
