package hn.uth.views.empleados;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

import hn.uth.controller.InteractorEmpleados;
import hn.uth.controller.InteractorImplEmpleados;
import hn.uth.data.Empleado;
import hn.uth.data.SamplePerson;
import hn.uth.views.MainLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Empleados")
@Route(value = "empleados/:identidad?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Uses(Icon.class)
public class EmpleadosView extends Div implements BeforeEnterObserver, ViewModelEmpleados {

    private final String EMPLOYEE_ID = "identidad";
    private final String EMPLOYEE_EDIT_ROUTE_TEMPLATE = "empleados/%s/edit";

    private final Grid<Empleado> grid = new Grid<>(Empleado.class, false);

    private TextField nombre;
    private TextField apellido;
    private TextField identidad;
    private TextField telefono;
    private TextField horario;
    private TextField puesto;
    private NumberField sueldo;

    private final Button cancel = new Button("Cancelar");
    private final Button save = new Button("Guardar", new Icon(VaadinIcon.CHECK_CIRCLE));
    private final Button eliminar = new Button("Eliminar", new Icon(VaadinIcon.TRASH));

    private Empleado empleadoSeleccionado;
    private List<Empleado> elementos;
    private InteractorEmpleados controlador;

    public EmpleadosView() {
        addClassNames("empleados-view");
        
        controlador = new InteractorImplEmpleados(this);
        elementos = new ArrayList<>();

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("identidad").setAutoWidth(true);
        grid.addColumn("nombre").setAutoWidth(true);
        grid.addColumn("apellido").setAutoWidth(true);
        grid.addColumn("telefono").setAutoWidth(true);
        grid.addColumn("horario").setAutoWidth(true);
        grid.addColumn("puesto").setAutoWidth(true);
        grid.addColumn("sueldo").setAutoWidth(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(EMPLOYEE_EDIT_ROUTE_TEMPLATE, event.getValue().getIdentidad()));
            } else {
                clearForm();
                UI.getCurrent().navigate(EmpleadosView.class);
            }
        });

        controlador.consultarEmpleados();

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.empleadoSeleccionado == null) {
                    this.empleadoSeleccionado = new Empleado();
                }
                
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(EmpleadosView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        eliminar.addClickListener( e-> {
        	 Notification n = Notification.show("Botón eliminar seleccionado, aún no hay nada que eliminar");
        	 n.setPosition(Position.MIDDLE);
             n.addThemeVariants(NotificationVariant.LUMO_WARNING);
        });
    }

    //METODO QUE SE EJECUTA AL SELECCIONAR UN ELEMENTO DEL GRID
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> identidadEmpleado = event.getRouteParameters().get(EMPLOYEE_ID);
        if (identidadEmpleado.isPresent()) {
            Empleado empledoObtenido = obtenerEmpleado(identidadEmpleado.get());
            if (empledoObtenido != null) {
                populateForm(empledoObtenido);
            } else {
                Notification.show(
                        String.format("El empleado con Identidad = %s no existe", identidadEmpleado.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(EmpleadosView.class);
            }
        }
    }

    private Empleado obtenerEmpleado(String identidad) {
		Empleado encontrado = null;
    	for(Empleado emp: elementos) {
			if(emp.getIdentidad().equals(identidad)) {
				encontrado = emp;
				break;
			}
		}
		return encontrado;
	}

	private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        nombre = new TextField("Nombre");
        nombre.setId("txt_nombre");
        nombre.setPrefixComponent(VaadinIcon.USER_CHECK.create());
        
        
        apellido = new TextField("Apellido");
        apellido.setId("txt_apellido");
        identidad = new TextField("Identidad");
        identidad.setId("txt_identidad");
        identidad.setPrefixComponent(VaadinIcon.USER_CARD.create());
        
        
        telefono = new TextField("Telefono");
        telefono.setId("txt_telefono");
        horario = new TextField("Horario");
        horario.setId("txt_horario");
        puesto = new TextField("Puesto");
        puesto.setId("txt_puesto");
        sueldo = new NumberField("Sueldo");
        sueldo.setId("txt_sueldo");
        sueldo.setLabel("Sueldo");
        sueldo.setValue(0.0);
        Div lempiraPrefix = new Div();
        lempiraPrefix.setText("L.");
        sueldo.setPrefixComponent(lempiraPrefix);
        
        //METODO ADD (AGREGA UN CONTROL A LA PANTALLA O DENTRO DE OTRO CONTROL)
        //SI EL ADD ESTÁ SOLO, LO AGREGA DIRECTAMENTE EN LA PANTALLA
        //SI EL ADD ESTA DE ESTA FORMA {CONTROL}.ADD LO AGREGA DENTRO DE {CONTROL}
        formLayout.add(identidad, nombre, apellido, telefono, horario, puesto, sueldo);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.setId("btn_cancelar");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.setId("btn_guardar");
        eliminar.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        eliminar.setId("btn_eliminar");
        
        
        buttonLayout.add(save, eliminar, cancel);
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

    private void populateForm(Empleado value) {
        this.empleadoSeleccionado = value;
        if(value != null) {
        	nombre.setValue(value.getNombre());
            apellido.setValue(value.getApellido());
            identidad.setValue(value.getIdentidad());
            telefono.setValue(value.getTelefono());
            horario.setValue(value.getHorario());
            puesto.setValue(value.getPuesto());
            sueldo.setValue(value.getSueldo());
        }else {
        	nombre.setValue("");
            apellido.setValue("");
            identidad.setValue("");
            telefono.setValue("");
            horario.setValue("");
            puesto.setValue("");
            sueldo.setValue(0.0);
        }     
    }

	@Override
	public void mostrarEmpleadosEnGrid(List<Empleado> items) {
		Collection<Empleado> itemsCollection = items;
		grid.setItems(itemsCollection);
		this.elementos = items;
	}

	@Override
	public void mostrarMensajeError(String mensaje) {
		Notification.show(mensaje);
	}
}
