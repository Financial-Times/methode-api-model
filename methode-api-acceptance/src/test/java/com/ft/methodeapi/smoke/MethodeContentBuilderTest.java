package com.ft.methodeapi.smoke;

import com.ft.methodeapi.acceptance.MethodeContent;
import com.ft.methodeapi.acceptance.ReferenceArticles;
import com.ft.methodeapi.acceptance.ReferenceLists;
import com.ft.methodeapi.model.LinkedObject;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

/**
 * MethodeContentBuilderTest
 *
 * @author Simon.Gibbs
 */
public class MethodeContentBuilderTest {

    public static final String EDITED_HEADLINE = "Changed";
    
    static final List<LinkedObject> LINKED_OBJECTS = Collections.singletonList(new LinkedObject(UUID.randomUUID().toString(), "EOM::CompoundStory"));
    
    @Test
    public void builtArticleShouldContainExampleHeadlineByDefault() {
        assertThat(ReferenceArticles.publishedKitchenSinkArticle().build().getArticleXml(),containsString(MethodeContent.HEADLINE_FROM_TEST_FILE));
    }

    @Test
    public void builtArticleAttributesShouldContainExampleHeadlineByDefault() {
        assertThat(ReferenceArticles.publishedKitchenSinkArticle().build().getAttributesXml(),containsString(MethodeContent.HEADLINE_FROM_TEST_FILE));
    }

    @Test
    public void builtArticleShouldNotContainExampleHeadlineWhenChanged() {
        String articleXml = ReferenceArticles.publishedKitchenSinkArticle().withHeadline(EDITED_HEADLINE).build().getArticleXml();
        assertThat(articleXml,not(containsString(MethodeContent.HEADLINE_FROM_TEST_FILE)));
        assertThat(articleXml,containsString(EDITED_HEADLINE));
    }

    @Test
    public void builtArticleAttributesShouldNotContainExampleHeadlineWhenChanged() {
        String attributesXml = ReferenceArticles.publishedKitchenSinkArticle().withHeadline(EDITED_HEADLINE).build().getAttributesXml();
        assertThat(attributesXml,not(containsString(MethodeContent.HEADLINE_FROM_TEST_FILE)));
        assertThat(attributesXml,containsString(EDITED_HEADLINE));
    }

	@Test
	public void builtArticleShouldHaveCorrectTypeValue() {
		assertThat(ReferenceArticles.publishedKitchenSinkArticle().build().getEomFile().getType(), containsString("EOM::CompoundStory"));
	}

	@Test
	public void builtListShouldHaveCorrectTypeValue() {
		assertThat(ReferenceLists.publishedList(LINKED_OBJECTS).build().getEomFile().getType(), equalTo("EOM::WebContainer"));
	}

	@Test
	public void builtArticleShouldHaveCorrectWorkflowStatus() {
		assertThat(ReferenceArticles.publishedKitchenSinkArticle().build().getWorkflowStatus(), containsString(MethodeContent.WEB_READY));
	}

	@Test
	public void builtArticleShouldHaveChangedWorkflowStatus() {
		String workflowStatus = ReferenceArticles.publishedKitchenSinkArticle().withWorkflowStatus(MethodeContent.WEB_REVISE).build().getWorkflowStatus();
		assertThat(workflowStatus, is(MethodeContent.WEB_REVISE));
	}

	@Test
	public void builtListShouldHaveCorrectWorkflowStatus() {
		assertThat(ReferenceLists.publishedList(LINKED_OBJECTS).build().getWorkflowStatus(), containsString(MethodeContent.CLEARED));
	}

	@Test
	public void builtArticleShouldHaveChangedSource() {
		String newSource = "Reuters";
		String newSourceXml = String.format("<Source title=\"%s\"><SourceCode>%s</SourceCode><SourceDescriptor>%s</SourceDescriptor>", newSource, newSource, newSource);

		String attributesXml = ReferenceArticles.publishedKitchenSinkArticle().withSource(newSource).build().getAttributesXml();
		assertThat(attributesXml, CoreMatchers.containsString(newSourceXml));
	}

	@Test
	public void builtArticleShouldHaveChangedEmbargoDate() {
		Date embargoDate = new Date();
		String embargoDateAsString = inMethodeFormat(embargoDate);
		String newSourceXml = String.format("<EmbargoDate>%s</EmbargoDate>", embargoDateAsString);

		String attributesXml = ReferenceArticles.publishedKitchenSinkArticle().withEmbargoDate(embargoDate).build().getAttributesXml();
		assertThat(attributesXml, CoreMatchers.containsString(newSourceXml));
	}

	private String inMethodeFormat(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		DateFormat methodeDateFormat = new SimpleDateFormat(MethodeContent.METHODE_DATE_FORMAT);
		methodeDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return methodeDateFormat.format(cal.getTime());
	}
}
