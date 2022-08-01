package model.services;

import java.util.List;


import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;


public class DepartmentService {
	private DepartmentDao dao= DaoFactory.createDepartmentDao();
	public List<Department> findAll(){
//		List<Department> list=new ArrayList<>();
//		list.add(new Department(1, "Book"));
//		list.add(new Department(2, "Computers"));
//		list.add(new Department(3, "Eletronics"));
		return dao.findAll();
		
	}
	//esse metodo vai inserir um department ou autualizar caso ja exista o Id
	public void saveOrUpdate(Department obj) {
		if(obj.getId()==null) {
			dao.insert(obj);
		}else {
			dao.update(obj);
		}
	}
		public void remove(Department obj) {
			dao.deleteById(obj.getId());
		}
}
