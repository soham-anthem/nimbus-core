/**
 * 
 */
package com.anthem.oss.nimbus.core.domain.command;

import java.io.Serializable;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.anthem.oss.nimbus.core.domain.command.CommandElement.Type;
import com.anthem.oss.nimbus.core.domain.definition.Constants;
import com.anthem.oss.nimbus.core.util.CollectionsTemplate;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Soham Chakravarti
 *
 */
@Data @ToString(of={"absoluteUri", "event", "behaviors", "currentBehaviorIndex", "clientUserId"}) 
public class Command implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String absoluteUri;
	
	private String clientUserId;

	@Getter(value=AccessLevel.PROTECTED)
	private CommandElementLinked root;
	
	private Action action;
	
	private String event;
	
	private int currentBehaviorIndex = -1; // -1 to size()
	
	private List<Behavior> behaviors;
	
	private Map<String, String[]> requestParams;
	
	@JsonIgnore
	final private Instant createdInstant = Instant.now();
	
	@JsonIgnore @Getter(value=AccessLevel.PRIVATE)
	final private transient CollectionsTemplate<List<Behavior>, Behavior> templateBehaviors = CollectionsTemplate.linked(()->getBehaviors(), s->setBehaviors(s));
	
	
	public CollectionsTemplate<List<Behavior>, Behavior> templateBehaviors() {
		return templateBehaviors;
	}
	
	public boolean isRootDomainOnly() {
		return !root().findFirstMatch(Type.DomainAlias).hasNext();
	}

	public CommandElementLinked root() {
		return root;
	}
	
	public boolean isEvent() {
		return StringUtils.trimToNull(getEvent()) != null;
	}

	
	public Command createRootDomainCommand() {
		String cUri = buildUri(getRoot(), Type.DomainAlias);
		Command c = CommandBuilder.withUri(cUri).getCommand();
		//shallowCopy(c);
		return c;
	}
	
	@Override
	public Command clone() {
		Command cloned = new Command(getAbsoluteUri());
		shallowCopy(cloned);
		
		CommandElementLinked clonedRoot = getRoot().clone();
		cloned.setRoot(clonedRoot);
		return cloned;
	}
	
	public void shallowCopy(Command to) {
		to.setAction(getAction());
		to.setEvent(getEvent());
		to.setBehaviors(getBehaviors());
		to.setClientUserId(getClientUserId());
	}
	
	
	public Command createNewCommandForCurrentUser(String newRootDomain, Action newAction, LinkedList<Behavior> newBehaviors) {
		StringBuilder newRootDomainFlowAlias = new StringBuilder();
		newRootDomainFlowAlias.append(newRootDomain);
		StringBuilder absoluteUri = new StringBuilder();
		CommandElementLinked clonedRoot = getRoot().cloneUpto(Type.DomainAlias,absoluteUri);
		CommandElementLinked platformMarker = clonedRoot.findFirstMatch(Type.PlatformMarker);
		platformMarker.createNext(Type.DomainAlias, newRootDomainFlowAlias.toString());
		absoluteUri.append(Constants.SEPARATOR_URI.code).append(newRootDomainFlowAlias);
		absoluteUri.append(Constants.SEPARATOR_URI.code).append(newAction).append("?b=").append(newBehaviors.peek());
		
		Command cloned = new Command(absoluteUri.toString());
		cloned.setAction(newAction);
		cloned.setEvent(getEvent());
		cloned.setBehaviors(newBehaviors);
		cloned.setClientUserId(getClientUserId());
		cloned.setRoot(clonedRoot);
		return cloned;
	}
	
	public Behavior getCurrentBehavior() {
		if(currentBehaviorIndex == -1 && CollectionUtils.isNotEmpty(getBehaviors()))
			return incrementAndGetCurrentBehavior();
		
		
		return (currentBehaviorIndex > -1 && currentBehaviorIndex < getBehaviors().size())
				? getBehaviors().get(currentBehaviorIndex) : null;
	}
	
	public Behavior incrementAndGetCurrentBehavior() {
		if(currentBehaviorIndex < getBehaviors().size()) 
			currentBehaviorIndex++;
		
		return getCurrentBehavior();
	}
	

	public String getAliasUri(Type type) {
		return getElement(type).map(e -> e.getAliasUri()).orElse(null);
	}
	
	public String getRefId(Type type) {
		return getElement(type).map(e -> e.getRefId()).orElse(null);
	}
	
	public String getAbsoluteUri(Type type) {
		return getElement(type).map(e -> e.getUri()).orElse(null);
	}

	public String getAlias(Type type) {
		return getElement(type).map(e -> e.getAlias()).orElse(null);
	}

	public Optional<CommandElement> getElement(Type type) {
		return Optional.ofNullable(root().findFirstMatch(type));
	}


	
/* TODO Refactor -- START -- */ 
	public boolean isView() {
		String domainRoot = getRootDomainAlias();
		return StringUtils.startsWith(domainRoot, Constants.PREFIX_FLOW.code);
	}

	public String getAppAlias() {
		return getAlias(Type.AppAlias);
	}
	
	public String getRootClientAlias() {
		return getAlias(Type.ClientAlias);
	}
	public CommandElement getRootDomainElement() {
		return getElement(Type.DomainAlias).get();
	}

	public String getRootDomainAlias() {
		return getRootDomainElement().getAlias();
	}

	public String getRootDomainUri() {
		return getRootDomainElement().getUri();
	}

	public String getAbsoluteDomainAlias() {
		String a = buildAlias(root().findFirstMatch(Type.DomainAlias));
		return a;
	}

	public String getAbsoluteDomainUri() {
		String u = buildUri(root().findFirstMatch(Type.DomainAlias));
		return u;
	}

	
	public String getProcessAlias() {
		String a = buildAlias(root().findFirstMatch(Type.ProcessAlias));
		return a;
	}
	
	
	public String getProcessUri() {
		String u = buildUri(root().findFirstMatch(Type.ProcessAlias));
		return u;
	}

	public String getAbsoluteAlias() {
		String a = buildAlias(root());
		return a;
	}

	public String getAbsoluteAliasTillRootDomain() {
		String a = buildAlias(root(), Type.DomainAlias);
		return a;
	}
/* TODO Refactor -- END -- */
	
	public String buildAlias(CommandElementLinked startElem) {
		return traverseElements(startElem, (cmdElem, sb) -> sb.append(cmdElem.getAliasUri()));
	}
	public String buildAlias(Type endWhentype) {
		return traverseElements(root(), endWhentype, (cmdElem, sb) -> sb.append(cmdElem.getAliasUri()));
	}
	public String buildAlias(CommandElementLinked startElem, Type endWhentype) {
		return traverseElements(startElem, endWhentype, (cmdElem, sb) -> sb.append(cmdElem.getAliasUri()));
	}
	
	public String buildUri(CommandElementLinked startElem) {
		return traverseElements(startElem, (cmdElem, sb) -> sb.append(cmdElem.getUri()));
	}
	public String buildUri(Type endWhenType) {
		return traverseElements(root(), endWhenType, (cmdElem, sb) -> sb.append(cmdElem.getUri()));
	}
	public String buildUri(CommandElementLinked startElem, Type endWhenType) {
		return traverseElements(startElem, endWhenType, (cmdElem, sb) -> sb.append(cmdElem.getUri()));
	}
	
	
	public String traverseElements(CommandElementLinked startElem, BiConsumer<CommandElement, StringBuilder> cb) {
		StringBuilder sb = new StringBuilder();
		traverseElements(startElem, (cmdElem) -> cb.accept(cmdElem, sb));
		return sb.toString();
	}
	
	public void traverseElements(CommandElementLinked startElem, Consumer<CommandElement> cb) {
		while (startElem != null) {
			cb.accept(startElem);
			startElem = startElem.next();
		}
	}
	
	public String traverseElements(CommandElementLinked startElem, Type type, BiConsumer<CommandElement, StringBuilder> cb) {
		StringBuilder sb = new StringBuilder();
		traverseElements(startElem, type, (cmdElem) -> cb.accept(cmdElem, sb));
		return sb.toString();
	}
	
	public void traverseElements(CommandElementLinked startElem, Type type, Consumer<CommandElement> cb) {
		while (startElem != null) {
			cb.accept(startElem);
			if (startElem.getType().equals(type)) {
				break;
			}
			startElem = startElem.next();
		}
	}

	

	public CommandElementLinked createRoot(Type type, String uri) {
		CommandElementLinked root = new CommandElementLinked();
		root.setType(type);
		root.setUri(uri);
		setRoot(root);
		
		return getRoot();
	}

	public String toUri() {
		String baseUri = buildUri(getRoot());
		StringBuilder sb = new StringBuilder(baseUri);
		
		/* action */
		sb.append(Constants.SEPARATOR_URI.code).append(getAction().name());
		
		/* event */
		if(isEvent()) {
			sb.append(Constants.SEPARATOR_URI.code).append(getEvent());	
		}
		
		/* behavior(s) */
		sb.append("?").append(Constants.MARKER_URI_BEHAVIOR.code).append("=");	//	?b=
		sb.append(getBehaviors().get(0).name());	// $execute (or other behavior)	
		
		getBehaviors().stream().sequential().skip(1).forEach(b->{
			sb.append(Constants.SEPARATOR_AND.code).append(b.name());
		});
		
		/* TODO: other request params */
		
		return sb.toString();
	}
	
	public String toUniqueId() {
		String uri = toUri();
		StringBuilder sb = new StringBuilder(uri);
		
		/* user id */
		if(StringUtils.trimToNull(getClientUserId()) != null) {
			sb.append(Constants.SEPARATOR_UNIQUE_KEYGEN.code).append(getClientUserId());
		}
		
		/* created instant */
		sb.append(Constants.SEPARATOR_UNIQUE_KEYGEN.code).append(getCreatedInstant());
		
		return sb.toString();
	}
}
