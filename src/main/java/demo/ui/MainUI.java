package demo.ui;

import com.vaadin.annotations.Title;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import demo.github.Commit;
import demo.github.GithubClient;

@Title("Vaadin with RestTemplate demo")
@SpringUI
public class MainUI extends UI {

	private static final long serialVersionUID = 1L;

	private Grid<Commit> commits = new Grid<>();

	private TextField organization = new TextField("Organization:", "vaadin");

	private TextField project = new TextField("Project:", "spring");

	private Button refresh = new Button("", this::refresh);

	private final GithubClient githubClient;

	public MainUI(GithubClient c) {
		this.githubClient = c;
	}

	@Override
	protected void init(VaadinRequest request) {
		commits.addComponentColumn(this::createLink).setExpandRatio(1).setCaption("Message");
        commits.addColumn(commit -> commit.getCommitter().getName()).setCaption("Committer");
        commits.setWidth("100%");

		refresh.setIcon(VaadinIcons.REFRESH);
		refresh.addStyleName(ValoTheme.BUTTON_PRIMARY);

		HorizontalLayout horizontalLayout = new HorizontalLayout(
				organization, project, refresh
		);
		horizontalLayout.setComponentAlignment(refresh, Alignment.BOTTOM_LEFT);

		VerticalLayout mainLayout = new VerticalLayout(
				new Label("Vaadin with RestTemplate demo"),
				horizontalLayout
		);

		mainLayout.addComponentsAndExpand(commits);

		setContent(mainLayout);
		listCommits();
	}

	private void listCommits() {
		commits.setItems(githubClient.getRecentCommits(
				organization.getValue(), project.getValue()));
	}

	public void refresh(ClickEvent clickEvent) {
		listCommits();
	}

	private Link createLink(Commit c) {
		String url = String.format("https://github.com/%s/%s/commit/%s",
				organization.getValue(), project.getValue(), c.getSha());
		Link link = new Link(c.getMessage(), new ExternalResource(url));
		link.setDescription(c.getSha());
		link.setStyleName(ValoTheme.LINK_SMALL);
		return link;
	}

}
