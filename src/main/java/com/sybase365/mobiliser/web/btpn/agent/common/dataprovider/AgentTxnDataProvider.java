package com.sybase365.mobiliser.web.btpn.agent.common.dataprovider;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import com.sybase365.mobiliser.web.btpn.agent.beans.AgentTxnDataBean;

public class AgentTxnDataProvider extends SortableDataProvider<AgentTxnDataBean>{
	private static final long serialVersionUID = 1L;

	private List<AgentTxnDataBean> txnDataList;

	public List<AgentTxnDataBean> getTxnDataList() {
		return txnDataList;
	}

	public void setTxnDataList(List<AgentTxnDataBean> txnDataList) {
		this.txnDataList = txnDataList;
	}

	/**
	 * This is the constructor with default Sort Property
	 */
	public AgentTxnDataProvider(String defaultSortProperty,
			final List<AgentTxnDataBean> txnDataList) {
		this.txnDataList = txnDataList;
		setSort(defaultSortProperty, false);
	}

	/**
	 * Overriden the iterator method
	 */
	@Override
	public Iterator<? extends AgentTxnDataBean> iterator(int first, int count) {
		SortParam sp = getSort();
		return find(first, count, sp.getProperty(), sp.isAscending())
				.iterator();
	}

	/**
	 * Defining the loadable detachable model
	 */
	@Override
	public IModel<AgentTxnDataBean> model(final AgentTxnDataBean object) {
		IModel<AgentTxnDataBean> model = new LoadableDetachableModel<AgentTxnDataBean>() {
			private static final long serialVersionUID = 1L;

			@Override
			protected AgentTxnDataBean load() {
				AgentTxnDataBean set = null;
				for (AgentTxnDataBean beanVar : txnDataList) {
					if (beanVar.equals(object)) {
						set = object;
						break;
					}
				}
				return set;
			}
		};
		return new CompoundPropertyModel<AgentTxnDataBean>(model);
	}

	/**
	 * returns the size of the data list
	 */
	@Override
	public int size() {
		int count = 0;
		if (txnDataList == null) {
			return count;
		}
		return txnDataList.size();
	}

	/**
	 * returns the sorted list
	 */
	protected List<AgentTxnDataBean> find(int first, int count,
			String sortProperty, boolean sortAsc) {
		List<AgentTxnDataBean> sublist = getIndex(txnDataList, sortProperty,
				sortAsc).subList(first, first + count);
		return sublist;
	}

	/**
	 * returns the index
	 */
	protected List<AgentTxnDataBean> getIndex(List<AgentTxnDataBean> txnDataList,
			String prop, boolean asc) {
		return sort(txnDataList, prop, asc);

	}

	/**
	 * returns the sorted list
	 */
	private List<AgentTxnDataBean> sort(List<AgentTxnDataBean> entries,
			String property, boolean asc) {
		if (property.equals("agentId")) {
			sortByAgentId(entries, asc);
		} else if (property.equals("date")) {
			sortByDate(entries, asc);
		} else if (property.equals("payer")) {
			sortByPayer(entries, asc);
		} else if (property.equals("txnId")) {
			sortByTxnId(entries, asc);
		} else if (property.equals("txnType")) {
			sortByTxnType(entries, asc);
		} else if (property.equals("consumerMobile")) {
			sortByConsumerMobile(entries, asc);
		} else if (property.equals("amount")) {
			sortByAmount(entries, asc);
		} else if(property.equals("owner")){
			sortByOwner(entries, asc);
		} else{
			sortByStatus(entries,asc);
		}
		return entries;
	}

	private void sortByAgentId(List<AgentTxnDataBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<AgentTxnDataBean>() {

				@Override
				public int compare(AgentTxnDataBean arg0, AgentTxnDataBean arg1) {
					return (arg0).getAgentId().compareTo(
							(arg1).getAgentId());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<AgentTxnDataBean>() {

				@Override
				public int compare(AgentTxnDataBean arg0, AgentTxnDataBean arg1) {
					return (arg1).getAgentId().compareTo(
							(arg0).getAgentId());
				}
			});
		}
	}

	
	private void sortByDate(List<AgentTxnDataBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<AgentTxnDataBean>() {

				@Override
				public int compare(AgentTxnDataBean arg0, AgentTxnDataBean arg1) {
					return (arg0).getDate().compareTo((arg1).getDate());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<AgentTxnDataBean>() {

				@Override
				public int compare(AgentTxnDataBean arg0, AgentTxnDataBean arg1) {
					return (arg1).getDate().compareTo((arg0).getDate());
				}
			});
		}
	}

	private void sortByPayer(List<AgentTxnDataBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<AgentTxnDataBean>() {

				@Override
				public int compare(AgentTxnDataBean arg0, AgentTxnDataBean arg1) {
					return (arg0).getPayer().compareTo((arg1).getPayer());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<AgentTxnDataBean>() {

				@Override
				public int compare(AgentTxnDataBean arg0, AgentTxnDataBean arg1) {
					return (arg1).getPayer().compareTo((arg0).getPayer());
				}
			});
		}
	}

	private void sortByAmount(List<AgentTxnDataBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<AgentTxnDataBean>() {

				@Override
				public int compare(AgentTxnDataBean arg0, AgentTxnDataBean arg1) {
					return String.valueOf((arg0).getAmount()).compareTo(
							String.valueOf((arg1).getAmount()));
				}
			});
		} else {
			Collections.sort(entries, new Comparator<AgentTxnDataBean>() {

				@Override
				public int compare(AgentTxnDataBean arg0, AgentTxnDataBean arg1) {
					return String.valueOf((arg1).getAmount()).compareTo(
							String.valueOf((arg0).getAmount()));
				}
			});
		}
	}

	private void sortByTxnId(List<AgentTxnDataBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<AgentTxnDataBean>() {

				@Override
				public int compare(AgentTxnDataBean arg0, AgentTxnDataBean arg1) {
					return (arg0).getTxnId().compareTo((arg1).getTxnId());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<AgentTxnDataBean>() {

				@Override
				public int compare(AgentTxnDataBean arg0, AgentTxnDataBean arg1) {
					return (arg1).getTxnId().compareTo((arg0).getTxnId());
				}
			});
		}
	}

	private void sortByConsumerMobile(List<AgentTxnDataBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<AgentTxnDataBean>() {

				@Override
				public int compare(AgentTxnDataBean arg0, AgentTxnDataBean arg1) {
					return (arg0).getConsumerMobile().compareTo(
							(arg1).getConsumerMobile());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<AgentTxnDataBean>() {

				@Override
				public int compare(AgentTxnDataBean arg0, AgentTxnDataBean arg1) {
					return (arg1).getConsumerMobile().compareTo(
							(arg0).getConsumerMobile());
				}
			});
		}
	}

	private void sortByStatus(List<AgentTxnDataBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<AgentTxnDataBean>() {

				@Override
				public int compare(AgentTxnDataBean arg0, AgentTxnDataBean arg1) {
					return (arg0).getStatus().compareTo((arg1).getStatus());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<AgentTxnDataBean>() {

				@Override
				public int compare(AgentTxnDataBean arg0, AgentTxnDataBean arg1) {
					return (arg1).getStatus().compareTo((arg0).getStatus());
				}
			});
		}
	}

	private void sortByTxnType(List<AgentTxnDataBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<AgentTxnDataBean>() {

				@Override
				public int compare(AgentTxnDataBean arg0, AgentTxnDataBean arg1) {
					return (arg0).getTxnType().compareTo((arg1).getTxnType());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<AgentTxnDataBean>() {

				@Override
				public int compare(AgentTxnDataBean arg0, AgentTxnDataBean arg1) {
					return (arg1).getTxnType().compareTo((arg0).getTxnType());
				}
			});
		}
	}
	private void sortByOwner(List<AgentTxnDataBean> entries, boolean asc) {
		if (asc) {
			Collections.sort(entries, new Comparator<AgentTxnDataBean>() {

				@Override
				public int compare(AgentTxnDataBean arg0, AgentTxnDataBean arg1) {
					return (arg0).getOwner().compareTo((arg1).getOwner());
				}
			});
		} else {
			Collections.sort(entries, new Comparator<AgentTxnDataBean>() {

				@Override
				public int compare(AgentTxnDataBean arg0, AgentTxnDataBean arg1) {
					return (arg1).getOwner().compareTo((arg0).getOwner());
				}
			});
		}
	}


}
