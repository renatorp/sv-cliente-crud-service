package br.pucminas.prod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.pucminas.prod.entity.Cliente;

@SpringBootApplication
@RestController
@RequestMapping(value = "/clientes")
public class ClienteCrudServiceApplication {

	private static int nextId = 1;
	private static Map<Integer, Cliente> poolClientes = new HashMap<>();
	
	@RequestMapping("/")
	@ResponseBody
	public List<Cliente> pesquisar(Cliente cliente) {
		return poolClientes.values().stream().filter(new Predicate<Cliente>() {

			@Override
			public boolean test(Cliente t) {
				return (StringUtils.isEmpty(cliente.getNome()) || t.getNome().contains(cliente.getNome()) 
						&& cliente.getId() == null || t.getId().equals(cliente.getId()));
			}
		}).collect(Collectors.toList());
	}

	@RequestMapping("/{id}")
	@ResponseBody
	public Cliente findById(@PathVariable Integer id) {
		return poolClientes.get(id);
	}
	
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	public Cliente criar(@RequestBody Cliente cliente) {
		cliente.setId(nextId++);
		poolClientes.put(cliente.getId(), cliente);
		return cliente;
	}
	
	@RequestMapping(value = "/", method = RequestMethod.PUT)
	@ResponseBody
	public Cliente atualizar(@RequestBody Cliente cliente) {
		Cliente persistedCliente = poolClientes.get(cliente.getId());
		BeanUtils.copyProperties(cliente, persistedCliente);
		return cliente;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public Cliente remover(@PathVariable Integer id) {
		return poolClientes.remove(id);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(ClienteCrudServiceApplication.class, args);
	}
}
