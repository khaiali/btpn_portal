package com.sybase365.mobiliser.web.common.panels;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.sybase365.mobiliser.util.tools.wicketutils.ErrorIndicator;

/**
 * @author <a href='mailto:Mark.DeVries@sybase.com'>Mark De Vries</a>
 * 
 */
public class SimpleCronPanel extends Panel {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(SimpleCronPanel.class);

    private static final long serialVersionUID = 1L;

    private String cronSchedule = "";
    WebMarkupContainer cronScheduleField;

    /**
     * @param name
     */
    public SimpleCronPanel(String name) {
	super(name);

	addOrReplace(getMinuteForm());
	addOrReplace(getHourForm());
	addOrReplace(getDailyForm());
	addOrReplace(getWeekdaysForm());
	addOrReplace(getSimpleMonthlyForm());
	addOrReplace(getAdvancedMonthlyForm());

    }

    final List<Integer> hours = Arrays
	    .asList(new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
		    13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23 });

    protected Form<?> getMinuteForm() {

	Form<?> form = new Form("minuteForm") {

	    private static final long serialVersionUID = 1L;
	    Integer minutes = Integer.valueOf(1);
	    Integer start = Integer.valueOf(0);

	    @Override
	    protected void onBeforeRender() {

		addOrReplace(new Label("mLabel1", new Model<String>(
			getLocalizer().getString("report.batch.exec", this))));
		addOrReplace(new TextField<Integer>("minuteText",
			new PropertyModel<Integer>(this, "minutes"))
			.setRequired(true).add(new ErrorIndicator()));
		addOrReplace(new Label("mLabel2", new Model<String>(
			getLocalizer().getString("report.batch.minutes", this))));
		addOrReplace(new AjaxButton("minuteButton", this) {

		    @Override
		    protected void onSubmit(AjaxRequestTarget target,
			    Form<?> form) {
			StringBuffer cron = new StringBuffer();
			cron.append("0").append(" ");
			cron.append(start + "/" + minutes.toString()).append(
				" ");
			cron.append("*").append(" ");
			cron.append("*").append(" ");
			cron.append("*").append(" ");
			cron.append("?");

			LOG.debug(cron.toString());
			cronSchedule = cron.toString();
			target.addComponent(cronScheduleField);
			target.appendJavascript("cronJobWizardOverlay.close();");
		    }

		});

		super.onBeforeRender();
	    }

	};

	return form;

    }

    protected Form<?> getHourForm() {

	Form<?> form = new Form("hourForm") {

	    private static final long serialVersionUID = 1L;
	    Integer hour = Integer.valueOf(1);

	    @Override
	    protected void onBeforeRender() {

		addOrReplace(new Label("hLabel1", new Model<String>(
			getLocalizer().getString("report.batch.exec", this))));
		addOrReplace(new TextField<Integer>("hourText",
			new PropertyModel<Integer>(this, "hour")).setRequired(
			true).add(new ErrorIndicator()));
		addOrReplace(new Label("hLabel2", new Model<String>(
			getLocalizer().getString("report.batch.hours", this))));

		addOrReplace(new AjaxButton("hourButton", this) {

		    @Override
		    protected void onSubmit(AjaxRequestTarget target,
			    Form<?> form) {

			StringBuffer cron = new StringBuffer();
			cron.append("0").append(" ");
			cron.append("0").append(" ");
			cron.append(0 + "/" + hour.toString()).append(" ");
			cron.append("*").append(" ");
			cron.append("*").append(" ");
			cron.append("?");
			LOG.debug(cron.toString());
			cronSchedule = cron.toString();
			target.addComponent(cronScheduleField);
			target.appendJavascript("cronJobWizardOverlay.close();");
		    }
		}

		);

		super.onBeforeRender();
	    }

	};

	return form;

    }

    protected Form<?> getDailyForm() {

	Form<?> form = new Form("dailyForm") {

	    private static final long serialVersionUID = 1L;
	    Integer days = Integer.valueOf(1);
	    Integer hour = Integer.valueOf(12);

	    @Override
	    protected void onBeforeRender() {

		addOrReplace(new Label("dLabel1", new Model<String>(
			getLocalizer().getString("report.batch.exec", this))));
		addOrReplace(new TextField<Integer>("dailyText",
			new PropertyModel<Integer>(this, "days")).setRequired(
			true).add(new ErrorIndicator()));
		addOrReplace(new Label("dLabel2", new Model<String>(
			getLocalizer().getString("report.batch.days", this))));
		addOrReplace(new Label("dLabel3", new Model<String>(
			getLocalizer().getString("report.batch.at", this))));

		addOrReplace(new DropDownChoice<Integer>("dailyHourText",
			new PropertyModel<Integer>(this, "hour"), hours)
			.setRequired(true).add(new ErrorIndicator()));
		addOrReplace(new Label("dLabel4", new Model<String>(
			getLocalizer().getString("report.batch.oclock", this))));

		addOrReplace(new AjaxButton("dailyButton", this) {

		    @Override
		    protected void onSubmit(AjaxRequestTarget target,
			    Form<?> form) {
			StringBuffer cron = new StringBuffer();
			cron.append("0").append(" ");
			cron.append("0").append(" ");
			cron.append(hour).append(" ");
			cron.append("1/" + days).append(" ");
			cron.append("*").append(" ");
			cron.append("?");
			LOG.debug(cron.toString());
			cronSchedule = cron.toString();
			target.addComponent(cronScheduleField);
			target.appendJavascript("cronJobWizardOverlay.close();");
		    }

		});

		super.onBeforeRender();
	    }

	};

	return form;

    }

    protected Form<?> getWeekdaysForm() {

	Form<?> form = new Form("weekdayForm") {

	    private static final long serialVersionUID = 1L;

	    boolean mon = false;
	    boolean tue = false;
	    boolean wed = false;
	    boolean thu = false;
	    boolean fri = false;
	    boolean sat = false;
	    boolean sun = false;

	    Integer hour = Integer.valueOf(12);

	    @Override
	    protected void onBeforeRender() {

		addOrReplace(new Label("wkdLabel1", new Model<String>(
			getLocalizer().getString("report.batch.exec", this))));
		addOrReplace(new CheckBox("mondayCheck",
			new PropertyModel<Boolean>(this, "mon"))
			.add(new ErrorIndicator()));
		addOrReplace(new CheckBox("tuesdayCheck",
			new PropertyModel<Boolean>(this, "tue"))
			.add(new ErrorIndicator()));
		addOrReplace(new CheckBox("wednesdayCheck",
			new PropertyModel<Boolean>(this, "wed"))
			.add(new ErrorIndicator()));
		addOrReplace(new CheckBox("thursdayCheck",
			new PropertyModel<Boolean>(this, "thu"))
			.add(new ErrorIndicator()));
		addOrReplace(new CheckBox("fridayCheck",
			new PropertyModel<Boolean>(this, "fri"))
			.add(new ErrorIndicator()));
		addOrReplace(new CheckBox("saturdayCheck",
			new PropertyModel<Boolean>(this, "sat"))
			.add(new ErrorIndicator()));
		addOrReplace(new CheckBox("sundayCheck",
			new PropertyModel<Boolean>(this, "sun"))
			.add(new ErrorIndicator()));

		addOrReplace(new Label("wkdLabelMon", new Model<String>(
			getLocalizer().getString("report.batch.monday", this))));
		addOrReplace(new Label("wkdLabelTue", new Model<String>(
			getLocalizer().getString("report.batch.tuesday", this))));
		addOrReplace(new Label("wkdLabelWed", new Model<String>(
			getLocalizer()
				.getString("report.batch.wednesday", this))));
		addOrReplace(new Label("wkdLabelThu",
			new Model<String>(getLocalizer().getString(
				"report.batch.thursday", this))));
		addOrReplace(new Label("wkdLabelFri", new Model<String>(
			getLocalizer().getString("report.batch.friday", this))));
		addOrReplace(new Label("wkdLabelSat",
			new Model<String>(getLocalizer().getString(
				"report.batch.saturday", this))));
		addOrReplace(new Label("wkdLabelSun", new Model<String>(
			getLocalizer().getString("report.batch.sunday", this))));

		addOrReplace(new Label("wkdLabel2", new Model<String>(
			getLocalizer().getString("report.batch.at", this))));

		addOrReplace(new DropDownChoice<Integer>("wkdHourText",
			new PropertyModel<Integer>(this, "hour"), hours)
			.setRequired(true).add(new ErrorIndicator()));
		addOrReplace(new Label("wkdLabel3", new Model<String>(
			getLocalizer().getString("report.batch.oclock", this))));

		addOrReplace(new AjaxButton("wkdButton", this) {

		    @Override
		    protected void onSubmit(AjaxRequestTarget target,
			    Form<?> form) {
			StringBuffer cron = new StringBuffer();
			cron.append("0").append(" ");
			cron.append("0").append(" ");
			cron.append(hour).append(" ");
			cron.append("?").append(" ");
			cron.append("*").append(" ");
			cron.append(generateCheckedDayExpression()).append(" ");
			cron.append("*");
			LOG.debug(cron.toString());
			cronSchedule = cron.toString();
			target.addComponent(cronScheduleField);
			target.appendJavascript("cronJobWizardOverlay.close();");
		    }

		});

		super.onBeforeRender();
	    }

	    private String generateCheckedDayExpression() {
		StringBuffer sb = new StringBuffer();
		if (mon)
		    sb.append("MON,");
		if (tue)
		    sb.append("TUE,");
		if (wed)
		    sb.append("WED,");
		if (thu)
		    sb.append("THU,");
		if (fri)
		    sb.append("FRI,");
		if (sat)
		    sb.append("SAT,");
		if (sun)
		    sb.append("SUN,");

		// remove last ","
		if (sb.length() > 0) {
		    sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	    }

	};

	return form;

    }

    protected Form<?> getSimpleMonthlyForm() {

	Form<?> form = new Form("monthlyForm") {

	    private static final long serialVersionUID = 1L;
	    Integer month = Integer.valueOf(1);
	    Integer days = Integer.valueOf(1);
	    Integer hour = Integer.valueOf(12);

	    @Override
	    protected void onBeforeRender() {

		addOrReplace(new Label("monthLabel1", new Model<String>(
			getLocalizer().getString("report.batch.exonday", this))));
		addOrReplace(new TextField<Integer>("monthDayText",
			new PropertyModel<Integer>(this, "days")).setRequired(
			true).add(new ErrorIndicator()));
		addOrReplace(new Label("monthLabel2", new Model<String>(
			getLocalizer().getString("report.batch.ofEvery", this))));
		addOrReplace(new TextField<Integer>("monthText",
			new PropertyModel<Integer>(this, "month")).setRequired(
			true).add(new ErrorIndicator()));
		addOrReplace(new Label("monthLabel3", new Model<String>(
			getLocalizer().getString("report.batch.months", this))));
		addOrReplace(new Label("monthLabel4", new Model<String>(
			getLocalizer().getString("report.batch.at", this))));

		addOrReplace(new DropDownChoice<Integer>("monthHourText",
			new PropertyModel<Integer>(this, "hour"), hours)
			.setRequired(true).add(new ErrorIndicator()));

		addOrReplace(new Label("monthLabel5", new Model<String>(
			getLocalizer().getString("report.batch.oclock", this))));

		addOrReplace(new AjaxButton("monthButton", this) {
		    @Override
		    protected void onSubmit(AjaxRequestTarget target,
			    Form<?> form) {
			StringBuffer cron = new StringBuffer();
			cron.append("0").append(" ");
			cron.append("0").append(" ");
			cron.append(hour).append(" ");
			cron.append(days).append(" ");
			cron.append("1/" + month).append(" ");
			cron.append("?").append(" ");
			cron.append("*");
			LOG.debug(cron.toString());
			cronSchedule = cron.toString();
			target.addComponent(cronScheduleField);
			target.appendJavascript("cronJobWizardOverlay.close();");
		    }
		});

		super.onBeforeRender();
	    }

	};

	return form;

    }

    protected Form<?> getAdvancedMonthlyForm() {

	Form<?> form = new Form("monthlyAdvancedForm") {

	    private static final long serialVersionUID = 1L;

	    Integer month = Integer.valueOf(1);

	    Integer hour = Integer.valueOf(12);

	    CronWeekDay weekday;
	    CronWeeks week;

	    final List<CronWeekDay> weekdays = Arrays.asList(new CronWeekDay[] {
		    CronWeekDay.Monday, CronWeekDay.Tuesday,
		    CronWeekDay.Wednesday, CronWeekDay.Thursday,
		    CronWeekDay.Friday, CronWeekDay.Saturday,
		    CronWeekDay.Sunday });

	    final List<CronWeeks> weeks = Arrays.asList(new CronWeeks[] {
		    CronWeeks.First, CronWeeks.Second, CronWeeks.Third,
		    CronWeeks.Fourth });

	    @Override
	    protected void onBeforeRender() {

		addOrReplace(new Label("monthAdvLabel1", new Model<String>(
			getLocalizer().getString("report.batch.exonthe", this))));
		addOrReplace(new DropDownChoice<CronWeeks>("monthWeekText",
			new PropertyModel<CronWeeks>(this, "week"), weeks,
			new IChoiceRenderer<CronWeeks>() {
			    /**
			     *
			     */
			    private static final long serialVersionUID = 1L;

			    @Override
			    public String getDisplayValue(CronWeeks object) {
				return object.getWeekDisplay();
			    }

			    @Override
			    public String getIdValue(CronWeeks object, int index) {
				return object.getWeekValue();
			    }

			}).setRequired(true).add(new ErrorIndicator()));

		addOrReplace(new DropDownChoice<CronWeekDay>("advDayText",
			new PropertyModel<CronWeekDay>(this, "weekday"),
			weekdays, new IChoiceRenderer<CronWeekDay>() {
			    /**
			     *
			     */
			    private static final long serialVersionUID = 1L;

			    @Override
			    public String getDisplayValue(CronWeekDay object) {
				return object.getDayDisplay();
			    }

			    @Override
			    public String getIdValue(CronWeekDay object,
				    int index) {
				return object.getDayId();
			    }

			}).setRequired(true).add(new ErrorIndicator()));

		addOrReplace(new Label("monthAdvLabel3", new Model<String>(
			getLocalizer().getString("report.batch.ofEvery", this))));
		addOrReplace(new TextField<Integer>("monthAdvText",
			new PropertyModel<Integer>(this, "month")).setRequired(
			true).add(new ErrorIndicator()));
		addOrReplace(new Label("monthAdvLabel4", new Model<String>(
			getLocalizer().getString("report.batch.months", this))));
		addOrReplace(new Label("monthAdvLabel5", new Model<String>(
			getLocalizer().getString("report.batch.at", this))));
		addOrReplace(new DropDownChoice<Integer>("monthAdvHourText",
			new PropertyModel<Integer>(this, "hour"), hours)
			.setRequired(true).add(new ErrorIndicator()));
		addOrReplace(new Label("monthAdvLabel6", new Model<String>(
			getLocalizer().getString("report.batch.oclock", this))));
		addOrReplace(new AjaxButton("monthAdvButton", this) {

		    @Override
		    protected void onSubmit(AjaxRequestTarget target,
			    Form<?> form) {

			StringBuffer cron = new StringBuffer();
			cron.append("0").append(" ");
			cron.append("0").append(" ");
			cron.append(hour).append(" ");
			cron.append("?").append(" ");
			cron.append("1/" + month).append(" ");
			cron.append(weekday.getDayId() + week.getWeekValue())
				.append(" ");
			cron.append("*");
			LOG.debug(cron.toString());
			cronSchedule = cron.toString();
			target.addComponent(cronScheduleField);
			target.appendJavascript("cronJobWizardOverlay.close();");
		    }

		});

		super.onBeforeRender();
	    }

	};

	return form;

    }

    public enum CronWeekDay {
	Monday("Monday", "MON"), Tuesday("Tuesday", "TUE"), Wednesday(
		"Wednesday", "WED"), Thursday("Thursday", "THU"), Friday(
		"Friday", "FRI"), Saturday("Saturday", "SAT"), Sunday("Sunday",
		"SUN");

	private String dayId;
	private String dayDisplay;

	CronWeekDay(String dayDisplay, String dayId) {
	    this.dayDisplay = dayDisplay;
	    this.dayId = dayId;
	}

	/**
	 * @return the dayId
	 */
	public String getDayId() {
	    return dayId;
	}

	/**
	 * @return the dayDisplay
	 */
	public String getDayDisplay() {

	    return dayDisplay;
	}

    }

    public enum CronWeeks {
	First("First", "#1"), Second("Second", "#2"), Third("Third", "#3"), Fourth(
		"Fourth", "#4");
	private String weekValue;
	private String weekDisplay;

	CronWeeks(String weekDisplay, String weekValue) {
	    this.weekDisplay = weekDisplay;
	    this.weekValue = weekValue;
	}

	/**
	 * @return the weekValue
	 */
	public String getWeekValue() {
	    return weekValue;
	}

	/**
	 * @return the weekDisplay
	 */
	public String getWeekDisplay() {
	    return weekDisplay;
	}

    }

    /**
     * @return the cronSchedule
     */
    public String getCronSchedule() {
	return cronSchedule;
    }

    /**
     * @param cronSchedule
     *            the cronSchedule to set
     */
    public void setCronSchedule(String cronSchedule) {
	this.cronSchedule = cronSchedule;
    }

    /**
     * @return the cronScheduleField
     */
    public WebMarkupContainer getCronScheduleField() {
	return cronScheduleField;
    }

    /**
     * @param cronScheduleField
     *            the cronScheduleField to set
     */
    public void setCronScheduleField(WebMarkupContainer cronScheduleField) {
	this.cronScheduleField = cronScheduleField;
    }

}
