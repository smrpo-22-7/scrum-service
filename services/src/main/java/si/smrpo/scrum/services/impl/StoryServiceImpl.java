package si.smrpo.scrum.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.rest.services.Validator;
import si.smrpo.scrum.services.SprintService;
import si.smrpo.scrum.services.StoryService;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class StoryServiceImpl implements StoryService {

    private static final Logger LOG = LogManager.getLogger(SprintServiceImpl.class.getName());

    @Inject
    private EntityManager em;

    @Inject
    private SprintService ss;

    @Inject
    private Validator validator;


}
