package client.base;

import client.misc.ClientManager;
import shared.model.AbstractModelListener;
import shared.model.IModelListener;

/**
 * Base class for controllers
 */
public abstract class Controller extends AbstractModelListener implements IController, IModelListener
{
	
	private IView view;
	
	protected Controller(IView view)
	{
		ClientManager.getModel().registerListener(this);
		setView(view);
	}
	
	private void setView(IView view)
	{
		this.view = view;
	}
	
	@Override
	public IView getView()
	{
		return this.view;
	}
	
}

