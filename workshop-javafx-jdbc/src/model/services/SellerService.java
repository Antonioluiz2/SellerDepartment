package model.services;

import java.util.List;


import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;


public class SellerService {
	private SellerDao dao= DaoFactory.createSellerDao();
	public List<Seller> findAll(){
//		List<Seller> list=new ArrayList<>();
//		list.add(new Seller(1, "Book"));
//		list.add(new Seller(2, "Computers"));
//		list.add(new Seller(3, "Eletronics"));
		return dao.findAll();
		
	}
	//esse metodo vai inserir um Seller ou autualizar caso ja exista o Id
	public void saveOrUpdate(Seller obj) {
		if(obj.getId()==null) {
			dao.insert(obj);
			System.out.println("inserindo Update");
		}else {
			dao.update(obj);
		}
	}
		public void remove(Seller obj) {
			dao.deleteById(obj.getId());
		}
}
