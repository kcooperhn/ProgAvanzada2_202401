package hn.uth.views.puestos;

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
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

import hn.uth.controller.InteractorEmpleados;
import hn.uth.controller.InteractorImplEmpleados;
import hn.uth.controller.InteractorImplPuestos;
import hn.uth.controller.InteractorPuestos;
import hn.uth.data.Empleado;
import hn.uth.data.Puesto;
import hn.uth.views.MainLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Puestos")
@Route(value = "puestos/:sampleAddressID?/:action?(edit)", layout = MainLayout.class)
public class PuestosView extends Div implements BeforeEnterObserver, ViewModelPuestos {

    private final String SAMPLEADDRESS_ID = "sampleAddressID";
    private final String SAMPLEADDRESS_EDIT_ROUTE_TEMPLATE = "puestos/%s/edit";

    private final Grid<Puesto> grid = new Grid<>(Puesto.class, false);

    private NumberField idpuesto;
    private TextField nombre;
    private TextField departamento;

    private final Button cancel = new Button("Cancelar");
    private final Button save = new Button("Guardar");

    private Puesto puesto;
    private List<Puesto> elementos;
    private InteractorPuestos controlador;

    public PuestosView() {
        addClassNames("puestos-view");

        controlador = new InteractorImplPuestos(this);
        elementos = new ArrayList<>();
        
        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("idpuesto").setAutoWidth(true);
        grid.addColumn("nombre").setAutoWidth(true);
        grid.addColumn("departamento").setAutoWidth(true);
  
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SAMPLEADDRESS_EDIT_ROUTE_TEMPLATE, event.getValue().getIdpuesto()));
            } else {
                clearForm();
                UI.getCurrent().navigate(PuestosView.class);
            }
        });
        
        controlador.consultarPuestos();

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.puesto == null) {
                    this.puesto = new Puesto();
                    
                    this.puesto.setNombre(nombre.getValue());
                    this.puesto.setDepartamento(departamento.getValue());
                    
                    this.controlador.crearPuesto(puesto);
                }else {
                	this.puesto.setIdpuesto(idpuesto.getValue().intValue());
                	this.puesto.setNombre(nombre.getValue());
                    this.puesto.setDepartamento(departamento.getValue());
                    
                    this.controlador.actualizarPuesto(puesto);
                }
                clearForm();
                refreshGrid();
                UI.getCurrent().navigate(PuestosView.class);
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
        Optional<Long> idPuesto = event.getRouteParameters().get(SAMPLEADDRESS_ID).map(Long::parseLong);
        if (idPuesto.isPresent()) {
        	Puesto puestoObtenido = obtenerPuesto(idPuesto.get());
        	if (puestoObtenido != null) {
                populateForm(puestoObtenido);
            } else {
                Notification.show(
                        String.format("El puesto seleccionado no existe, ID = %s", idPuesto.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(PuestosView.class);
            }
        }
    }

    private Puesto obtenerPuesto(Long idpuesto) {
    	Puesto encontrado = null;
    	for(Puesto pst: elementos) {
			if(pst.getIdpuesto() == idpuesto) {
				encontrado = pst;
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
        idpuesto = new NumberField("ID");
        nombre = new TextField("Nombre");
        departamento = new TextField("Departamento");
        formLayout.add(idpuesto, nombre, departamento);

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
        this.controlador.consultarPuestos();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Puesto value) {
        this.puesto = value;
        if(value != null) {
        	idpuesto.setValue(Double.valueOf(value.getIdpuesto()));
            nombre.setValue(value.getNombre());
            departamento.setValue(value.getDepartamento());

        }else {
        	idpuesto.setValue(0.0);
        	nombre.setValue("");
        	departamento.setValue("");
        }  
    }

	@Override
	public void mostrarPuestosEnGrid(List<Puesto> items) {
		Collection<Puesto> itemsCollection = items;
		grid.setItems(itemsCollection);
		this.elementos = items;
	}

	@Override
	public void mostrarMensajeError(String mensaje) {
		Notification.show(mensaje);
	}
	
	@Override
	public void mostrarMensajeExito(String mensaje) {
		Notification.show(mensaje);
	}
}
