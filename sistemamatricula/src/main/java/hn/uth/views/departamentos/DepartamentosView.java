package hn.uth.views.departamentos;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

import hn.uth.data.Departamento;
import hn.uth.data.Puesto;
import hn.uth.views.MainLayout;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Departamentos")
@Route(value = "departamentos/:sampleAddressID?/:action?(edit)", layout = MainLayout.class)
public class DepartamentosView extends Div implements BeforeEnterObserver {

    private final String SAMPLEADDRESS_ID = "sampleAddressID";
    private final String SAMPLEADDRESS_EDIT_ROUTE_TEMPLATE = "departamentos/%s/edit";

    private final Grid<Departamento> grid = new Grid<>(Departamento.class, false);

    private TextField street;
    private TextField postalCode;
    private TextField city;
    private TextField state;
    private TextField country;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private Departamento sampleAddress;


    public DepartamentosView() {
        addClassNames("departamentos-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("street").setAutoWidth(true);
        grid.addColumn("postalCode").setAutoWidth(true);
        grid.addColumn("city").setAutoWidth(true);
        grid.addColumn("state").setAutoWidth(true);
        grid.addColumn("country").setAutoWidth(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SAMPLEADDRESS_EDIT_ROUTE_TEMPLATE, event.getValue().getIdDepartamento()));
            } else {
                clearForm();
                UI.getCurrent().navigate(DepartamentosView.class);
            }
        });

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.sampleAddress == null) {
                    this.sampleAddress = new Departamento();
                }


                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(DepartamentosView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> sampleAddressId = event.getRouteParameters().get(SAMPLEADDRESS_ID).map(Long::parseLong);
        if (sampleAddressId.isPresent()) {
            /*Optional<SampleAddress> sampleAddressFromBackend = sampleAddressService.get(sampleAddressId.get());
            if (sampleAddressFromBackend.isPresent()) {
                populateForm(sampleAddressFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested sampleAddress was not found, ID = %s", sampleAddressId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(DepartamentosView.class);
            }*/
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        street = new TextField("Street");
        postalCode = new TextField("Postal Code");
        city = new TextField("City");
        state = new TextField("State");
        country = new TextField("Country");
        formLayout.add(street, postalCode, city, state, country);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Departamento value) {
        this.sampleAddress = value;

    }
}
