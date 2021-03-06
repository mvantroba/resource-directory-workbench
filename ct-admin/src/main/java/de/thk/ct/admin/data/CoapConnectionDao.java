package de.thk.ct.admin.data;

import java.util.List;

import de.thk.ct.admin.model.CoapConnection;

public interface CoapConnectionDao {

	void beginTransaction();

	void commitTransaction();

	void create(CoapConnection coapConnection);

	List<CoapConnection> findAll();

	CoapConnection findById(CoapConnection coapConnection);

	void update(CoapConnection coapConnection);

	void delete(CoapConnection coapConnection);
}
