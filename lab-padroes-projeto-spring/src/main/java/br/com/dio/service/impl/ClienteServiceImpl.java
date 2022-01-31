package br.com.dio.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.dio.model.Cliente;
import br.com.dio.model.Endereco;
import br.com.dio.repository.ClienteRepository;
import br.com.dio.repository.EnderecoRepository;
import br.com.dio.service.ClienteService;
import br.com.dio.service.ViaCepService;

@Service
public class ClienteServiceImpl implements ClienteService{

	//TODO Singleton: Injetar os componentes do Spring com @Autowired.
	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private ViaCepService viaCepService;
	
	//TODO Strategy: Implementar os métodos definidos na interface.
	//TODO Facade: Abstrair integrações com subsistemas, provendo uma interface simples.
	
	@Override
	public Iterable<Cliente> buscarTodos() {
		return clienteRepository.findAll();
	}

	@Override
	public Cliente buscarPorId(Long id) {
		Optional<Cliente> cliente = clienteRepository.findById(id);
		return cliente.get();
	}

	@Override
	public void inserir(Cliente cliente) {
		salvarClienteComCep(cliente);
	}

	@Override
	public void atualizar(Long id, Cliente cliente) {
		// Buscar Cliente por ID, caso exista:
		Optional<Cliente> clienteBd = clienteRepository.findById(id);
		if(clienteBd.isPresent())
		{
			salvarClienteComCep(cliente);
		}
	}

	@Override
	public void deletar(Long id) {
		//Deletar Cliente por ID
		clienteRepository.deleteById(id);
	}
	
	private void salvarClienteComCep(Cliente cliente)
	{
		// Verificar se o Endereco do Cliente já existe (pelo CEP).
		String cep = cliente.getEndereco().getCep();
		Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
			// Caso não exista, integrar com o ViaCEP e presistir o retorno.
						Endereco novoEndereco = viaCepService.consultarCep(cep);
						enderecoRepository.save(novoEndereco);
						return novoEndereco;
		});
		cliente.setEndereco(endereco);
		// Inserir Cliente, vinculando o Endereco (novo ou existente).
		clienteRepository.save(cliente);
	}
}
