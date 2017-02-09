/**
 * 
 */
package com.anthem.nimbus.platform.spec.model.dsl.binder;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Optional;

import com.anthem.nimbus.platform.spec.model.command.Command;
import com.anthem.nimbus.platform.spec.model.dsl.config.Config;
import com.anthem.nimbus.platform.spec.model.dsl.config.ModelConfig;
import com.anthem.nimbus.platform.spec.model.dsl.config.ParamConfig;
import com.anthem.nimbus.platform.spec.model.dsl.config.ParamType;
import com.anthem.nimbus.platform.spec.model.util.CollectionsTemplate;
import com.anthem.nimbus.platform.spec.model.util.StateAndConfigSupportProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Soham Chakravarti
 *
 */
public interface DomainState<T> {

	public String getPath();
	
	public Config<T> getConfig();

	public <S> Model<S> findModelByPath(String path);
	public <S> Model<S> findModelByPath(String[] pathArr);

	public <P> Param<P> findParamByPath(String path);
	public <P> Param<P> findParamByPath(String[] pathArr);
	
    public <S> State<S> findStateByPath(String path);
	public <S> State<S> findStateByPath(String[] pathArr);

	public void init();
	
	public StateAndConfigSupportProvider getProvider();
	
	public RootModel<?> getRootModel();
	
	default public boolean isRoot() {
		return false;
	}
	
	default boolean isMapped() {
		return false;
	}
	default public Mapped<T, ?> findIfMapped() {
		return null;
	}
	
	
	public interface Mapped<T, M> extends DomainState<T> {
		@Override
		default boolean isMapped() {
			return true;
		}
		@Override
		default public Mapped<T, M> findIfMapped() {
			return this;
		}
		public DomainState<M> getMapsTo();
	}
	

	
	public interface RootModel<T> extends Model<T> {
		@Override
		default boolean isRoot() {
			return true;
		}
		
		@Override
		default RootModel<T> findIfRoot() {
			return this;
		}
		
		public Command getCommand();
		
		public ExecutionRuntime getExecutionRuntime();
		
		
		default public <U> U unwrap(Class<U> c) {
			if(c.isInstance(this))
				return c.cast(this);
			
			return null;
		}
	}
	
	public interface Model<T> extends DomainState<T> { //Notification.ObserveOn<MappedModel<?, T>, Model<T>> {
		@Override
		public ModelConfig<T> getConfig();
		
		public String getRootDomainUri();
		
		public Param<T> getAssociatedParam();
		
		default RootModel<T> findIfRoot() {
			return null;
		}
		
		@Override
		default public MappedModel<T, ?> findIfMapped() {
			return null;
		}
		
		public List<Param<? extends Object>> getParams();
		
		default public <K> ListModel<K> findIfListModel() {
			return null;
		}
		
		public CollectionsTemplate<List<Param<?>>, Param<?>> templateParams();
		
		public T instantiateOrGet();
		public T instantiateAndSet();
		
		default public T getState() {
			return Optional.ofNullable(getAssociatedParam()).map(p->p.getState()).orElse(null);
		}
		default public void setState(T state) {
			Optional.ofNullable(getAssociatedParam()).ifPresent(p->p.setState(state));
		}
		
		public ExecutionStateTree getExecutionStateTree();
	}
	
	public interface MappedModel<T, M> extends Model<T>, Mapped<T, M> {//, Notification.Subscriber<Model<M>> {
		@Override
		default public MappedModel<T, M> findIfMapped() {
			return this;
		}
		
		@Override
		public Model<M> getMapsTo();
	}
	
	public interface ListModel<T> extends Model<List<T>>, ListBehavior<T> {
		@Override
		default public MappedListModel<T, ?> findIfMapped() {
			return null;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		default ListModel<T> findIfListModel() {
			return this;
		}
		
		@Override
		public ListElemParam<T> add();
		
		default public ParamConfig<T> getElemConfig() {
			DomainStateType.NestedCollection<T> typeSAC = getAssociatedParam().getType().findIfCollection(); 
			ParamType.NestedCollection<T> typeConfig = typeSAC.getConfig().findIfCollection();
			
			ParamConfig<T> elemConfig = typeConfig.getElementConfig();
			return elemConfig;
		}
		
		public String toElemId(int i);
		public int fromElemId(String elemId);
		
	}
	public interface MappedListModel<T, M> extends ListModel<T>, MappedModel<List<T>, List<M>> {
		@Override
		default public MappedListModel<T, M> findIfMapped() {
			return this;
		}
		@Override
		public ListModel<M> getMapsTo();
	}
	
	public interface Param<T> extends DomainState<T>, State<T>, Notification.Producer<T> {//, Notification.ObserveOn<MappedParam<?, T>, Param<T>> {
		@Override
		public ParamConfig<T> getConfig();
		
		@JsonIgnore
		public <M> Model<M> getParentModel();
		
		public DomainStateType getType();
		
		default boolean isLeaf() {
			return getConfig().isLeaf();
		}
		
		default public MappedParam<T, ?> findIfMapped() {
			return null;
		}
		
		default public boolean isCollection() {
			return false;
		}
		
		default public boolean isNested() {
			return getType().isNested();
		}
		
		default public boolean isCollectionElem() {
			return false;
		}
		
		default public <K> ListParam<K> findIfCollection() {
			return null;
		}
		
		default public ListElemParam<T> findIfCollectionElem() {
			return null;
		}
		
		public PropertyDescriptor getPropertyDescriptor();

	}
	
	public interface MappedParam<T, M> extends Param<T>, Mapped<T, M>, Notification.Consumer<M> {
		@Override
		default public MappedParam<T, M> findIfMapped() {
			return this;
		}

		@Override
		public Param<M> getMapsTo();
		
		default public boolean requiresConversion() {
			if(isLeaf()) return false;
			
			Class<?> mappedClass = getType().findIfNested().getModel().getConfig().getReferredClass();
			Class<?> mapsToClass = getMapsTo().getType().findIfNested().getModel().getConfig().getReferredClass();

			// conversion required when mappedClass and mapsToClass are NOT same
			return (mappedClass!=mapsToClass);
		}
	}
	
	public interface ListBehavior<T> {
		/*
		public Param<T> get(int i);
		public boolean add(Param<T> p);
		public boolean remove(Param<T> p);
		public Param<T> remove(int i);
		public Param<T> set(int i, Param<T> p);*/
		
		public T get(int i);
		public boolean add(T elem);
		public Param<T> add();
		public int size();
	}
	
	
	public interface ListParam<T> extends Param<List<T>>, ListBehavior<T> {
		@Override
		public DomainStateType.NestedCollection<T> getType();
		
		@Override
		default public MappedListParam<T, ?> findIfMapped() {
			return null;
		}
		
		default public boolean isCollection() {
			return true;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		default ListParam<T> findIfCollection() {
			return this;
		}
		
		@Override
		public ListElemParam<T> add();
	}
	public interface MappedListParam<T, M> extends ListParam<T>, MappedParam<List<T>, List<M>> {
		@Override
		public ListParam<M> getMapsTo();
		
		@Override
		default public MappedListParam<T, M> findIfMapped() {
			return this;
		}

		@Override
		default boolean requiresConversion() {
			Class<?> mappedElemClass = getType().findIfCollection().getModel().getElemConfig().getReferredClass();
			Class<?> mapsToElemClass = getMapsTo().getType().findIfCollection().getModel().getElemConfig().getReferredClass();
			
			// conversion required when mappedClass and mapsToClass are NOT same
			return (mappedElemClass!=mapsToElemClass);
		}
	}
	
	public interface ListElemParam<E> extends Param<E> {
		public String getElemId();
		public int getElemIndex();
		
		@SuppressWarnings("unchecked")
		@Override
		public ListModel<E> getParentModel();
		
		@Override
		default public MappedListElemParam<E, ?> findIfMapped() {
			return null;
		}
		
		@Override
		default boolean isCollectionElem() {
			return true;
		}
		
		@Override
		default ListElemParam<E> findIfCollectionElem() {
			return this;
		}
	}	
	public interface MappedListElemParam<E, M> extends ListElemParam<E>, MappedParam<E, M> {
		@Override
		public ListElemParam<M> getMapsTo();
		
		@Override
		default public MappedListElemParam<E, M> findIfMapped() {
			return this;
		}
	}
	

}
